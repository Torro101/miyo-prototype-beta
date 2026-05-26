(() => {
  "use strict";

  const schemaVersion = 1;
  const graphState = {
    projectId: "local-placeholder",
    projectTitle: "Node Connect",
    nodes: [
      {
        id: "project-start",
        type: "start",
        title: "Start",
        subtitle: "Waiting for host graph",
        position: { x: 48, y: 96 },
        color: "#8fe3b0",
        badges: ["local"]
      }
    ],
    edges: [],
    viewport: { x: 0, y: 0, zoom: 1 },
    diagnosticsCount: 0
  };

  const bridgeLog = document.getElementById("bridgeLog");
  const exportButton = document.getElementById("exportButton");
  const fitButton = document.getElementById("fitButton");
  const edgesSvg = document.getElementById("edges");
  const graph = document.getElementById("graph");
  const nodeLayer = document.getElementById("nodeLayer");
  const projectTitle = document.getElementById("projectTitle");
  const nodeCount = document.getElementById("nodeCount");
  const edgeCount = document.getElementById("edgeCount");

  let selectedNodeId = null;
  let dragState = null;

  function graphSnapshot() {
    return {
      schemaVersion,
      projectId: graphState.projectId,
      projectTitle: graphState.projectTitle,
      nodes: graphState.nodes,
      edges: graphState.edges,
      viewport: graphState.viewport,
      diagnosticsCount: graphState.diagnosticsCount
    };
  }

  function bridgeMessage(type, payload) {
    return {
      schemaVersion,
      type,
      payload,
      sentAt: new Date().toISOString()
    };
  }

  function log(message) {
    bridgeLog.textContent = `${new Date().toLocaleTimeString()} ${message}\n${bridgeLog.textContent}`.slice(0, 2800);
  }

  function validateEdge(edge) {
    const ids = new Set(graphState.nodes.map((node) => node.id));
    return ids.has(edge.source) && ids.has(edge.target) && edge.source !== edge.target;
  }

  function nodeById(id) {
    return graphState.nodes.find((node) => node.id === id);
  }

  function nodeCenter(node, side) {
    const width = node.type === "scene" ? 210 : 220;
    const height = 116;
    return {
      x: Number(node.position?.x ?? 0) + (side === "out" ? width : 0),
      y: Number(node.position?.y ?? 0) + height / 2
    };
  }

  function drawEdges() {
    edgesSvg.replaceChildren();

    for (const edge of graphState.edges) {
      if (!validateEdge(edge)) {
        continue;
      }

      const source = nodeById(edge.source);
      const target = nodeById(edge.target);
      const start = nodeCenter(source, "out");
      const end = nodeCenter(target, "in");
      const mid = Math.max(70, Math.abs(end.x - start.x) / 2);
      const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
      path.classList.add("edge");
      path.dataset.edgeId = edge.id;
      path.setAttribute("stroke", edge.color || "#ff6f9d");
      path.setAttribute("d", `M ${start.x} ${start.y} C ${start.x + mid} ${start.y}, ${end.x - mid} ${end.y}, ${end.x} ${end.y}`);
      edgesSvg.appendChild(path);

      if (edge.label) {
        const label = document.createElementNS("http://www.w3.org/2000/svg", "text");
        label.classList.add("edge-label");
        label.setAttribute("x", String((start.x + end.x) / 2));
        label.setAttribute("y", String((start.y + end.y) / 2 - 8));
        label.textContent = edge.label.length > 18 ? `${edge.label.slice(0, 17)}...` : edge.label;
        edgesSvg.appendChild(label);
      }
    }
  }

  function renderNode(node) {
    const article = document.createElement("article");
    article.className = `node ${node.type || "utility"}`;
    article.dataset.nodeId = node.id;
    article.style.left = `${Number(node.position?.x ?? 0)}px`;
    article.style.top = `${Number(node.position?.y ?? 0)}px`;
    article.style.borderColor = node.color || "";
    article.style.setProperty("--node-accent", node.color || "#ff6f9d");
    article.tabIndex = 0;

    const inPort = document.createElement("span");
    inPort.className = "port in";
    inPort.setAttribute("aria-hidden", "true");
    article.appendChild(inPort);

    const outPort = document.createElement("span");
    outPort.className = "port out";
    outPort.setAttribute("aria-hidden", "true");
    article.appendChild(outPort);

    const header = document.createElement("div");
    header.className = "node-header";
    const type = document.createElement("span");
    type.className = "node-type";
    type.textContent = node.type || "node";
    const title = document.createElement("h2");
    title.textContent = node.title || node.id;
    header.append(type, title);
    article.appendChild(header);

    const subtitle = document.createElement("p");
    subtitle.textContent = node.subtitle || "";
    article.appendChild(subtitle);

    if (Array.isArray(node.badges) && node.badges.length > 0) {
      const badges = document.createElement("div");
      badges.className = "badges";
      node.badges.slice(0, 3).forEach((badge) => {
        const item = document.createElement("span");
        item.textContent = badge;
        badges.appendChild(item);
      });
      article.appendChild(badges);
    }

    article.addEventListener("click", () => selectNode(node.id));
    article.addEventListener("pointerdown", (event) => startDrag(event, node, article));
    return article;
  }

  function renderGraph() {
    nodeLayer.replaceChildren();
    projectTitle.textContent = graphState.projectTitle || "Node Connect";
    nodeCount.textContent = `${graphState.nodes.length} nodes`;
    edgeCount.textContent = `${graphState.edges.filter(validateEdge).length} edges`;

    for (const node of graphState.nodes) {
      nodeLayer.appendChild(renderNode(node));
    }

    selectedNodeId = selectedNodeId && nodeById(selectedNodeId) ? selectedNodeId : null;
    markSelection();
    drawEdges();
  }

  function selectNode(nodeId) {
    selectedNodeId = nodeId;
    markSelection();
    postToHost("graph.select", { nodeId });
  }

  function markSelection() {
    document.querySelectorAll(".node").forEach((element) => {
      element.classList.toggle("selected", element.dataset.nodeId === selectedNodeId);
    });
  }

  function startDrag(event, node, element) {
    if (event.button !== 0) return;
    element.setPointerCapture(event.pointerId);
    dragState = {
      pointerId: event.pointerId,
      nodeId: node.id,
      startPointerX: event.clientX,
      startPointerY: event.clientY,
      startNodeX: Number(node.position?.x ?? 0),
      startNodeY: Number(node.position?.y ?? 0)
    };
    selectNode(node.id);
    event.preventDefault();
  }

  function moveDrag(event) {
    if (!dragState || event.pointerId !== dragState.pointerId) return;
    const node = nodeById(dragState.nodeId);
    if (!node) return;
    const nextX = Math.max(8, Math.round(dragState.startNodeX + event.clientX - dragState.startPointerX));
    const nextY = Math.max(8, Math.round(dragState.startNodeY + event.clientY - dragState.startPointerY));
    node.position = { x: nextX, y: nextY };
    const element = Array.from(document.querySelectorAll(".node")).find((candidate) => candidate.dataset.nodeId === node.id);
    if (element) {
      element.style.left = `${nextX}px`;
      element.style.top = `${nextY}px`;
    }
    drawEdges();
  }

  function endDrag(event) {
    if (!dragState || event.pointerId !== dragState.pointerId) return;
    const node = nodeById(dragState.nodeId);
    dragState = null;
    if (node) {
      postToHost("graph.moveNodeCommit", {
        nodeId: node.id,
        position: node.position
      });
    }
  }

  function loadHostGraph(payload) {
    if (!payload || !Array.isArray(payload.nodes) || !Array.isArray(payload.edges)) {
      log("Ignored invalid host graph payload");
      return;
    }
    graphState.projectId = payload.projectId || graphState.projectId;
    graphState.projectTitle = payload.projectTitle || graphState.projectTitle;
    graphState.nodes = payload.nodes.map((node) => ({
      ...node,
      position: {
        x: Number(node.position?.x ?? 0),
        y: Number(node.position?.y ?? 0)
      },
      badges: Array.isArray(node.badges) ? node.badges : []
    }));
    graphState.edges = payload.edges;
    graphState.viewport = payload.viewport || graphState.viewport;
    graphState.diagnosticsCount = Number(payload.diagnosticsCount || 0);
    renderGraph();
    log(`Loaded host graph: ${graphState.nodes.length} nodes`);
  }

  function postToHost(type, payload) {
    const message = bridgeMessage(type, payload);
    const encoded = JSON.stringify(message);

    if (window.MiyoNodeBridge && typeof window.MiyoNodeBridge.postMessage === "function") {
      window.MiyoNodeBridge.postMessage(encoded);
      log(`Sent Android bridge message: ${type}`);
      return;
    }

    if (window.webkit?.messageHandlers?.miyoNodeBridge) {
      window.webkit.messageHandlers.miyoNodeBridge.postMessage(message);
      log(`Sent iOS bridge message: ${type}`);
      return;
    }

    log(`No host bridge available; local message only: ${encoded}`);
  }

  window.MiyoNodeConnect = {
    receiveHostMessage(message) {
      const envelope = typeof message === "string" ? JSON.parse(message) : message;
      if (!envelope || envelope.schemaVersion !== schemaVersion) {
        log("Ignored host message with unsupported schema");
        return;
      }
      if (envelope.type === "host.loadGraph") {
        loadHostGraph(envelope.payload);
      } else {
        log(`Host message received: ${envelope.type}`);
      }
    },
    exportSnapshot() {
      return graphSnapshot();
    }
  };

  exportButton.addEventListener("click", () => {
    postToHost("graph.snapshot", graphSnapshot());
  });

  fitButton.addEventListener("click", () => {
    graph.scrollTo({ left: 0, top: 0, behavior: "smooth" });
    postToHost("graph.viewportChanged", graphState.viewport);
  });

  graph.addEventListener("pointermove", moveDrag);
  graph.addEventListener("pointerup", endDrag);
  graph.addEventListener("pointercancel", endDrag);

  renderGraph();
  postToHost("web.ready", graphSnapshot());
})();
