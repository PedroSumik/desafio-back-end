const API_BASE = "/amostras";

const tabelaBody = document.getElementById("tabelaAmostras");
const searchInput = document.getElementById("searchInput");
const statusSelect = document.getElementById("statusSelect");
const btnBuscar = document.getElementById("btnBuscar");
const btnCriarAmostra = document.getElementById("btnCriarAmostra");

const modalDetalhes = document.getElementById("modalDetalhes");
const detalhesConteudo = document.getElementById("detalhesConteudo");

const modalCriar = document.getElementById("modalCriar");

let paginaAtual = 0;
let tamanhoPagina = 10;
let ordenacaoAtual = null;
let direcaoOrdenacao = "asc";

function formatarData(data) {
  if (!data) return "-";

  const d = new Date(data);
  if (isNaN(d.getTime())) return "-";

  const ano = d.getFullYear();
  const mes = String(d.getMonth() + 1).padStart(2, "0");
  const dia = String(d.getDate()).padStart(2, "0");
  const horas = String(d.getHours()).padStart(2, "0");
  const minutos = String(d.getMinutes()).padStart(2, "0");
  const segundos = String(d.getSeconds()).padStart(2, "0");

  return `${ano}-${mes}-${dia} ${horas}:${minutos}:${segundos}`;
}

function abrirModal(modal) {
  modal.style.display = "flex";
}

function fecharModal(modal) {
  modal.style.display = "none";
}

async function carregarAmostras() {
  const params = new URLSearchParams();

  if (searchInput.value.trim()) {
    params.append("codAmostra", searchInput.value.trim());
  }

  if (statusSelect.value) {
    params.append("status", statusSelect.value);
  }

  const dataInicio = document.getElementById("dataInicio");
  const dataFim = document.getElementById("dataFim");

  if (dataInicio && dataInicio.value) {
    params.append("inicio", dataInicio.value + "T00:00:00");
  }

  if (dataFim && dataFim.value) {
    params.append("fim", dataFim.value + "T23:59:59");
  }

  params.append("page", paginaAtual);
  params.append("size", tamanhoPagina);

  if (ordenacaoAtual) {
    params.append("sort", `${ordenacaoAtual},${direcaoOrdenacao}`);
  }

  try {
    const response = await fetch(`${API_BASE}?${params.toString()}`);
    const dados = await response.json();

    renderizarTabela(dados.content);
    renderizarPaginacao(dados);
  } catch (error) {
    console.error("Erro ao carregar amostras:", error);
    alert("Erro ao carregar amostras!");
  }
}

function renderizarTabela(amostras) {
  tabelaBody.innerHTML = "";

  if (amostras.length === 0) {
    const tr = document.createElement("tr");
    tr.innerHTML =
      '<td colspan="5" style="text-align: center;">Nenhuma amostra encontrada</td>';
    tabelaBody.appendChild(tr);
    return;
  }

  amostras.forEach((amostra) => {
    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td>${amostra.codAmostra}</td>
      <td>${amostra.tipoColeta || "-"}</td>
      <td>
        <span class="status ${amostra.status}">
          ${amostra.status}
        </span>
      </td>
      <td><i>${formatarData(amostra.dataColeta)}</i></td>
      <td>
        <button class="btn primary btn-detalhes" data-id="${amostra.id}">
          Ver Detalhes
        </button>
      </td>
    `;

    tabelaBody.appendChild(tr);
  });

  document.querySelectorAll(".btn-detalhes").forEach((btn) => {
    btn.addEventListener("click", () => {
      abrirDetalhes(btn.dataset.id);
    });
  });
}

function renderizarPaginacao(dados) {
  let paginacaoHTML = document.querySelector(".paginacao");

  if (!paginacaoHTML) {
    paginacaoHTML = document.createElement("div");
    paginacaoHTML.className = "paginacao";
    document.querySelector(".table-container").appendChild(paginacaoHTML);
  }

  const { totalPages, number: pageNumber, numberOfElements } = dados;

  let html = `
    <div style="text-align: center; margin-top: 20px; padding: 10px;">
      <span>Página ${pageNumber + 1} de ${totalPages} | Total: ${dados.totalElements} registros</span>
      <div style="margin-top: 10px;">
  `;

  if (pageNumber > 0) {
    html += `<button class="btn primary" onclick="irParaPagina(${pageNumber - 1})">← Anterior</button>`;
  }

  const inicio = Math.max(0, pageNumber - 2);
  const fim = Math.min(totalPages, pageNumber + 3);

  for (let i = inicio; i < fim; i++) {
    if (i === pageNumber) {
      html += `<button class="btn primary" style="background: #333;" disabled>${i + 1}</button>`;
    } else {
      html += `<button class="btn primary" onclick="irParaPagina(${i})">${i + 1}</button>`;
    }
  }

  if (pageNumber < totalPages - 1) {
    html += `<button class="btn primary" onclick="irParaPagina(${pageNumber + 1})">Próximo →</button>`;
  }

  html += `</div></div>`;
  paginacaoHTML.innerHTML = html;
}

function irParaPagina(pagina) {
  paginaAtual = pagina;
  carregarAmostras();
  window.scrollTo(0, 0);
}

function ordenarPor(coluna) {
  if (ordenacaoAtual === coluna) {
    if (direcaoOrdenacao === "asc") {
      direcaoOrdenacao = "desc";
    } else if (direcaoOrdenacao === "desc") {
      ordenacaoAtual = null;
      direcaoOrdenacao = "asc";
    }
  } else {
    ordenacaoAtual = coluna;
    direcaoOrdenacao = "asc";
  }
  paginaAtual = 0;
  carregarAmostras();
}

async function abrirDetalhes(id) {
  const response = await fetch(`${API_BASE}/${id}`);
  const amostra = await response.json();

  renderizarDetalhes(amostra);
  abrirModal(modalDetalhes);
}

function habilitarEdicao(status) {
  return status == "APROVADA" || status == "REJEITADA" ? "disabled" : "";
}

function renderizarDetalhes(amostra) {
  const historicoOrdenado = (amostra.historicoStatus || []).sort(
    (a, b) => new Date(a.data) - new Date(b.data),
  );

  detalhesConteudo.innerHTML = `
    <form id="formDetalhes">

      <div class="form-group">
        <label>Código da Amostra</label>
        <input type="text" id="codAmostra" value="${amostra.codAmostra}" ${habilitarEdicao(amostra.status)}/>
      </div>

      <div class="form-group">
        <label>Tipo da coleta</label>
        <input type="text" id="tipoColeta" value="${amostra.tipoColeta || ""}" ${habilitarEdicao(amostra.status)} />
      </div>

      <div class="form-group">
        <label>Data da coleta</label>
        <input type="date-time" id="dataColeta" value="${formatarData(amostra.dataColeta) || null}" ${habilitarEdicao(amostra.status)}/>
      </div>


      <div class="form-group">
        <label>Status Atual</label>
        <span class="status ${amostra.status}">
          ${amostra.status}
        </span>
      </div>

      <div class="actions">
        ${
          amostra.status === "CONCLUIDA"
            ? `
              <button type="button" class="btn success" onclick="aprovarStatus('${amostra.id}', 'APROVADA')">
                Aprovar
              </button>
              <button type="button" class="btn danger" onclick="rejeitarStatus('${amostra.id}', 'REJEITADA')">
                Rejeitar
              </button>
            `
            : amostra.status != "APROVADA" && amostra.status != "REJEITADA"
              ? `
              <button type="button" class="btn primary" onclick="atualizarStatus('${amostra.id}')">
                Atualizar Status
              </button>
            `
              : ""
        }
      </div>

      <h3>Histórico de Status</h3>
      <ul class="historico">
        ${historicoOrdenado
          .map(
            (h) => `
            <li>
            <i>${formatarData(h.dataAlteracao)} |</i>
              <strong>${h.codStatus}</strong>
            </li>
            </br>
          `,
          )
          .join("")}
      </ul>

      <div>
        ${
          amostra.status != "APROVADA" && amostra.status != "REJEITADA"
            ? `<button type="button" class="btn primary" onclick="atualizarAmostra('${amostra.id}', '${amostra.status}', '${amostra.dataColeta}')">Atualizar amostra</button>`
            : ""
        }
          
      </div>

    </form>
  `;
}

async function atualizarAmostra(id, status) {
  const tipoColeta = document.getElementById("tipoColeta").value;
  const codAmostra = document.getElementById("codAmostra").value;
  const dataColeta = document
    .getElementById("dataColeta")
    .value.substring(" ", "T");

  console.log("status", status);

  await fetch(`${API_BASE}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ id, status, tipoColeta, codAmostra, dataColeta }),
  });

  fecharModal(modalDetalhes);
  carregarAmostras();
}

async function atualizarStatus(id, status) {
  await fetch(`${API_BASE}/atualizar/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status }),
  });

  fecharModal(modalDetalhes);
  carregarAmostras();
}

async function rejeitarStatus(id, status) {
  await fetch(`${API_BASE}/rejeitar/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status }),
  });

  fecharModal(modalDetalhes);
  carregarAmostras();
}

async function aprovarStatus(id, status) {
  await fetch(`${API_BASE}/aprovar/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status }),
  });

  fecharModal(modalDetalhes);
  carregarAmostras();
}

btnBuscar.addEventListener("click", () => {
  paginaAtual = 0;
  carregarAmostras();
});

statusSelect.addEventListener("change", () => {
  paginaAtual = 0;
  carregarAmostras();
});

btnCriarAmostra.addEventListener("click", () => {
  abrirModalCriar();
});

document.querySelectorAll(".close-btn").forEach((btn) => {
  btn.addEventListener("click", (e) => {
    const modal = e.target.closest(".modal-overlay");
    fecharModal(modal);
  });
});

function abrirModalCriar() {
  const formCriar = document.querySelector("#modalCriar form");

  if (!formCriar) {
    console.warn("Formulário de criação não encontrado!");
    return;
  }

  const agora = new Date();
  const ano = agora.getFullYear();
  const mes = String(agora.getMonth() + 1).padStart(2, "0");
  const dia = String(agora.getDate()).padStart(2, "0");
  const horas = String(agora.getHours()).padStart(2, "0");
  const minutos = String(agora.getMinutes()).padStart(2, "0");
  const dataHoraAtual = `${ano}-${mes}-${dia}T${horas}:${minutos}`;

  formCriar.innerHTML = `
    <div class="form-group">
      <label>Código da Amostra *</label>
      <input type="text" id="criarCodAmostra" placeholder="Ex: AM001" required/>
    </div>

    <div class="form-group">
      <label>Tipo de Coleta *</label>
      <input type="text" id="criarTipoColeta" placeholder="Ex: SANGUE" required/>
    </div>

    <div class="form-group">
      <label>Data de Coleta *</label>
      <input type="datetime-local" id="criarDataColeta" value="${dataHoraAtual}" required/>
    </div>

    <div class="actions">
      <button type="submit" class="btn success">Criar Amostra</button>
      <button type="button" class="btn secondary" onclick="fecharModal(document.getElementById('modalCriar'))">Cancelar</button>
    </div>
  `;

  formCriar.addEventListener("submit", criarAmostra);
  abrirModal(modalCriar);
}

async function criarAmostra(e) {
  e.preventDefault();

  const codAmostra = document.getElementById("criarCodAmostra").value.trim();
  const tipoColeta = document.getElementById("criarTipoColeta").value.trim();
  const dataColeta = document.getElementById("criarDataColeta").value;

  if (!codAmostra || !tipoColeta || !dataColeta) {
    alert("Todos os campos são obrigatórios!");
    return;
  }

  try {
    const response = await fetch(API_BASE, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        codAmostra,
        tipoColeta,
        dataColeta: dataColeta.replace("T", "T"),
      }),
    });

    if (!response.ok) {
      const erro = await response.json();
      console.log(erro);
      alert(`Erro: ${erro.mensagem || "Falha ao criar amostra"}`);
      return;
    }

    fecharModal(modalCriar);
    paginaAtual = 0;
    carregarAmostras();
  } catch (error) {
    console.error("Erro ao criar amostra:", error);
    alert("Erro ao criar amostra!");
  }
}

function inicializarDatas() {
  const hoje = new Date();
  const anoHoje = hoje.getFullYear();
  const mesHoje = String(hoje.getMonth() + 1).padStart(2, "0");
  const diaHoje = String(hoje.getDate()).padStart(2, "0");
  const dataHoje = `${anoHoje}-${mesHoje}-${diaHoje}`;

  const umMesAtras = new Date();
  umMesAtras.setMonth(umMesAtras.getMonth() - 1);
  const anoMes = umMesAtras.getFullYear();
  const mesMes = String(umMesAtras.getMonth() + 1).padStart(2, "0");
  const diaMes = String(umMesAtras.getDate()).padStart(2, "0");
  const dataUmMesAtras = `${anoMes}-${mesMes}-${diaMes}`;

  const dataInicio = document.getElementById("dataInicio");
  const dataFim = document.getElementById("dataFim");

  if (dataInicio) dataInicio.value = dataUmMesAtras;
  if (dataFim) dataFim.value = dataHoje;
}

document.addEventListener("DOMContentLoaded", () => {
  inicializarDatas();

  const ths = document.querySelectorAll("table thead th");
  const colunas = ["codAmostra", "tipoColeta", "status", "dataColeta"];

  ths.forEach((th, index) => {
    if (index < colunas.length) {
      th.addEventListener("click", () => ordenarPor(colunas[index]));
    }
  });

  carregarAmostras();
});
