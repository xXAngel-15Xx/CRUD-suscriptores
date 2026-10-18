package suscripcion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Suscripcion extends JFrame implements ActionListener {

    JMenuBar bm;
    JMenu mBD;
    JMenuItem mBD_C, mBD_R, mBD_U, mBD_D;

    JLabel lbl, lbNombre, lbId, lbApellido, lbEdad, lbSexo;
    JTextField txtNombre, txtId, txtApellido, txtEdad;
    JComboBox<String> liSexo;

    JTable tablaSuscriptores;
    JScrollPane scrollTabla;
    DefaultTableModel modeloTabla;

    public Suscripcion() {
        this.setTitle("Gestor de Suscriptores");
        this.setBounds(150, 50, 650, 550);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);

        bm = new JMenuBar();
        this.setJMenuBar(bm);

        mBD = new JMenu("DataBase");
        bm.add(mBD);

        this.mBD_C = new JMenuItem("Crear");
        mBD.add(mBD_C);
        
        this.mBD_R = new JMenuItem("Consulta");
        mBD.add(mBD_R);
        
        this.mBD_U = new JMenuItem("Actualizar");
        mBD.add(mBD_U);
        
        this.mBD_D = new JMenuItem("Eliminar");
        mBD.add(mBD_D);

        this.lbl = new JLabel("Gestor de suscriptores");
        this.lbl.setBounds(230, 10, 200, 30);
        this.add(lbl);

        this.lbId = new JLabel("ID");
        this.lbId.setBounds(50, 50, 100, 30);
        this.add(lbId);

        this.txtId = new JTextField("");
        this.txtId.setBounds(160, 50, 350, 30);
        this.add(txtId);

        this.lbNombre = new JLabel("Nombre");
        this.lbNombre.setBounds(50, 90, 100, 30);
        this.add(lbNombre);

        this.txtNombre = new JTextField("");
        this.txtNombre.setBounds(160, 90, 350, 30);
        this.add(txtNombre);

        this.lbApellido = new JLabel("Apellido");
        this.lbApellido.setBounds(50, 130, 100, 30);
        this.add(lbApellido);

        this.txtApellido = new JTextField("");
        this.txtApellido.setBounds(160, 130, 350, 30);
        this.add(txtApellido);

        this.lbEdad = new JLabel("Edad");
        this.lbEdad.setBounds(50, 170, 100, 30);
        this.add(lbEdad);

        this.txtEdad = new JTextField("");
        this.txtEdad.setBounds(160, 170, 350, 30);
        this.add(txtEdad);

        this.lbSexo = new JLabel("Sexo");
        this.lbSexo.setBounds(50, 210, 100, 30);
        this.add(lbSexo);

        String[] opcion = {"", "Hombre", "Mujer"};
        this.liSexo = new JComboBox<>(opcion);
        this.liSexo.setBounds(160, 210, 350, 30);
        this.add(liSexo);

        String[] columnas = {"ID", "Nombre", "Apellido", "Edad", "Sexo"};
        modeloTabla = new DefaultTableModel(null, columnas);
        tablaSuscriptores = new JTable(modeloTabla);
        
        scrollTabla = new JScrollPane(tablaSuscriptores);
        scrollTabla.setBounds(50, 260, 530, 200);
        this.add(scrollTabla);

        //Escuchando al menu
        this.mBD_C.addActionListener(this);
        this.mBD_R.addActionListener(this);
        this.mBD_U.addActionListener(this);
        this.mBD_D.addActionListener(this);

        this.setVisible(true);
        
        consultar();
    }

    public static void main(String[] args) {
        Suscripcion menu = new Suscripcion();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.mBD_C) {
            this.agregar();
        }

        if (e.getSource() == this.mBD_R) {
            this.consultar();
        }

        if (e.getSource() == this.mBD_U) {
            this.actualizarRegistro();
        }

        if (e.getSource() == this.mBD_D) {
            this.eliminarRegistro();
        }
    }

    private Connection conectarBD() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/suscripcion?useSSL=false&serverTimezone=UTC", "root", "");
    }

    public void agregar() {
        if (this.txtNombre.getText().trim().isEmpty() || this.txtApellido.getText().trim().isEmpty() || this.txtEdad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Por favor, completa nombre, apellido y edad.");
            return;
        }

        if (this.liSexo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Por favor, selecciona una opción de sexo válida.");
            return;
        }

        try {
            Suscriptor suscriptor = new Suscriptor(
                0, 
                this.txtNombre.getText(),
                this.txtApellido.getText(),
                Integer.parseInt(this.txtEdad.getText()),
                this.liSexo.getSelectedItem().toString()
            );

            Connection cnx = conectarBD();
            PreparedStatement ps = cnx.prepareStatement("insert into suscripciones(nombre, apellido, edad, sexo) values(?, ?, ?, ?)");
            ps.setString(1, suscriptor.getNombre());
            ps.setString(2, suscriptor.getApellido());
            ps.setInt(3, suscriptor.getEdad());
            ps.setString(4, suscriptor.getSexo());
            ps.execute();

            JOptionPane.showMessageDialog(rootPane, "Registro agregado exitosamente.");
            cnx.close();
            limpiarCampos();
            consultar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(rootPane, "Por favor, introduce un número válido en Edad.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Error de Base de Datos: " + ex.getMessage());
        }
    }

    public void consultar() {
        try {
            modeloTabla.setRowCount(0);

            Connection cnx = conectarBD();
            PreparedStatement ps = cnx.prepareStatement("select * from suscripciones");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getInt("edad"),
                    rs.getString("sexo")
                };
                modeloTabla.addRow(fila);
            }

            cnx.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Error en la consulta: " + ex.getMessage());
        }
    }

    public void actualizarRegistro() {
        if (this.txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Ingresa el ID del suscriptor a modificar.");
            return;
        }

        if (this.liSexo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Por favor, selecciona una opción de sexo válida.");
            return;
        }

        try {
            Suscriptor suscriptor = new Suscriptor(
                Integer.parseInt(this.txtId.getText()),
                this.txtNombre.getText(),
                this.txtApellido.getText(),
                Integer.parseInt(this.txtEdad.getText()),
                this.liSexo.getSelectedItem().toString()
            );

            Connection cnx = conectarBD();
            PreparedStatement ps = cnx.prepareStatement("update suscripciones set nombre = ?, apellido = ?, edad = ?, sexo = ? where id = ?");
            ps.setString(1, suscriptor.getNombre());
            ps.setString(2, suscriptor.getApellido());
            ps.setInt(3, suscriptor.getEdad());
            ps.setString(4, suscriptor.getSexo());
            ps.setInt(5, suscriptor.getId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(rootPane, "Registro actualizado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(rootPane, "No se encontró ningún registro con ese ID.");
            }

            cnx.close();
            limpiarCampos();
            consultar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(rootPane, "Verifica que el ID y la edad sean numéricos.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, "Error al actualizar: " + ex.getMessage());
        }
    }

    public void eliminarRegistro() {
        if (this.txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Ingresa el ID del suscriptor a eliminar.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                rootPane,
                "¿Estás seguro de que deseas eliminar este registro?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                Connection cnx = conectarBD();
                PreparedStatement ps = cnx.prepareStatement("delete from suscripciones where id = ?");
                ps.setInt(1, Integer.parseInt(this.txtId.getText()));

                int filas = ps.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Registro eliminado exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "No se encontró el registro.");
                }

                cnx.close();
                limpiarCampos();
                consultar();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(rootPane, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        this.txtId.setText("");
        this.txtNombre.setText("");
        this.txtApellido.setText("");
        this.txtEdad.setText("");
        this.liSexo.setSelectedIndex(0); 
    }
}