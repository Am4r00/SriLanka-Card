document.addEventListener('DOMContentLoaded', async () => {
    await updateHeader();
    await carregarPedidos();
});

async function carregarPedidos() {
    const el = document.getElementById('pedidos-container');
    try {
        const pedidos = await apiRequest('/api/pedidos/historico');
        if (!pedidos || pedidos.length === 0) {
            el.innerHTML = '<p>Você ainda não tem pedidos.</p>';
            return;
        }
        el.innerHTML = pedidos.map(p => `
      <div class="pedido-card">
        <div><strong>Pedido #${p.id}</strong> - ${new Date(p.criadoEm).toLocaleString('pt-BR')}</div>
        <div>Total: R$ ${p.valorTotal?.toFixed(2) || '0,00'} | Itens: ${p.quantidadeTotal}</div>
        <details>
          <summary>Ver itens</summary>
          <ul>
            ${(p.itensPedido || []).map(i => `
              <li>${i.nomeProduto} - qtd ${i.quantidade} - R$ ${i.precoUnitario?.toFixed(2) || '0,00'}</li>
            `).join('')}
          </ul>
        </details>
      </div>
    `).join('');
    } catch (e) {
        el.innerHTML = `<p style="color:red;">Erro ao carregar pedidos: ${e.message}</p>`;
    }
}
