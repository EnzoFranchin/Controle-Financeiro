import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Main {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USUARIO = "system";
    private static final String SENHA = "15071977@";

    private static JTable tabelaContas;
    private static DefaultTableModel modeloTabela;
    private static JLabel lblResultado;

    public static void main(String[] args) {
        JFrame janela = new JFrame("Planilha de Gastos Diários e Fixos");
        janela.setSize(600, 500);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLayout(new BorderLayout(10, 10));
        janela.setLocationRelativeTo(null);

        JPanel painelTopo = new JPanel();
        painelTopo.setBackground(new Color(41, 128, 185));
        JLabel lblTitulo = new JLabel("DIGITE OS VALORES NA TABELA PARA CALCULAR O GASTO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);
        painelTopo.add(lblTitulo);
        janela.add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"Conta / Despesa", "Valor Digitado (R$)"};
        Object[][] dadosIniciais = {
                {"Aluguel / Moradia", "0.00"},
                {"Compras / Mercado", "0.00"},
                {"Internet / Wi-Fi", "0.00"},
                {"Luz / Energia", "0.00"},
                {"Água / Saneamento", "0.00"},
                {"Transporte / Combustível", "0.00"},
                {"Academia", "0.00"},
                {"App de filmes", "0.00"},
                {"Seguro do Carro", "0.00"},
                {"Outros Gastos", "0.00"}
        };

        modeloTabela = new DefaultTableModel(dadosIniciais, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        tabelaContas = new JTable(modeloTabela);
        tabelaContas.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaContas.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tabelaContas);
        janela.add(scroll, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new GridLayout(2, 1, 5, 5));
        painelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCalcular = new JButton("📊 Calcular Gasto Total e Salvar no Banco");
        btnCalcular.setFont(new Font("Arial", Font.BOLD, 14));
        btnCalcular.setBackground(new Color(46, 204, 113));
        btnCalcular.setForeground(Color.WHITE);
        painelInferior.add(btnCalcular);

        lblResultado = new JLabel("TOTAL DE GASTOS: R$ 0,00", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Arial", Font.BOLD, 18));
        lblResultado.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        painelInferior.add(lblResultado);

        janela.add(painelInferior, BorderLayout.SOUTH);

        btnCalcular.addActionListener(e -> calcularESalvarGastos());

        janela.setVisible(true);
    }

    private static void calcularESalvarGastos() {
        if (tabelaContas.isEditing()) {
            tabelaContas.getCellEditor().stopCellEditing();
        }

        double somaTotal = 0;
        int linhasSalvas = 0;

        try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA)) {
            String sql = "INSERT INTO transacoes (descricao, valor) VALUES (?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                    String conta = (String) modeloTabela.getValueAt(i, 0);
                    String valorStr = (String) modeloTabela.getValueAt(i, 1);

                    double valor = Double.parseDouble(valorStr.replace(",", "."));

                    if (valor > 0) {
                        somaTotal += valor;

                        pstmt.setString(1, conta);
                        pstmt.setDouble(2, valor);
                        pstmt.addBatch();
                        linhasSalvas++;
                    }
                }

                if (linhasSalvas > 0) {
                    pstmt.executeBatch();
                }
            }

            lblResultado.setText(String.format("TOTAL DE GASTOS: R$ %,.2f", somaTotal));
            JOptionPane.showMessageDialog(null, "Cálculo realizado! " + linhasSalvas + " contas foram salvas no banco de dados.");

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Erro: Certifique-se de usar apenas números e ponto para os centavos nas células!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ou salvar no banco:\n" + e.getMessage());
        }
    }
}