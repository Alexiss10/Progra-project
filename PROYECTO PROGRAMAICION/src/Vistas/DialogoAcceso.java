package Vistas;

import seguridad.GestionAcceso;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class DialogoAcceso extends JDialog {

    private final GestionAcceso acceso;

    private final JTextField txtUsuario = new JTextField(22);

    private final JPasswordField pwdClave
            = new JPasswordField(22);

    private final JPasswordField pwdConfirmacion
            = new JPasswordField(22);

    private final JLabel lblIndicacion
            = new JLabel("", SwingConstants.CENTER);

    private final JLabel lblConfirmacion
            = new JLabel("Confirmar contraseña:");

    private final JLabel lblEstado = new JLabel(" ");

    private final JButton btnAcceder = new JButton();
    private final JButton btnSalir = new JButton("Salir");

    private final Timer reloj;

    private boolean ocupado = false;

    public DialogoAcceso(Frame padre, GestionAcceso acceso) {

        super(padre, "Acceso a FILTEX", true);

        if (acceso == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar la gestión de acceso."
            );
        }

        this.acceso = acceso;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        construirInterfaz();

        btnAcceder.addActionListener(e -> procesarAcceso());
        btnSalir.addActionListener(e -> cerrar());

        getRootPane().setDefaultButton(btnAcceder);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });

        reloj = new Timer(1000, e -> {

            if (!ocupado) {
                actualizarEstado();
            }
        });

        configurarModo();
        actualizarEstado();

        pack();
        setSize(Math.max(500, getWidth()), getHeight());
        setLocationRelativeTo(padre);

        reloj.start();
    }

    private void construirInterfaz() {

        JPanel contenido = new JPanel(
                new BorderLayout(0, 18)
        );

        contenido.setBorder(
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        );

        JLabel titulo = new JLabel(
                "FILTEX",
                SwingConstants.CENTER
        );

        titulo.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 24)
        );

        JPanel encabezado = new JPanel(
                new GridLayout(2, 1, 0, 6)
        );

        encabezado.add(titulo);
        encabezado.add(lblIndicacion);

        JPanel campos = new JPanel(new GridBagLayout());

        agregarFila(
                campos, 0,
                new JLabel("Usuario:"),
                txtUsuario
        );

        agregarFila(
                campos, 1,
                new JLabel("Contraseña:"),
                pwdClave
        );

        agregarFila(
                campos, 2,
                lblConfirmacion,
                pwdConfirmacion
        );

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        botones.add(btnAcceder);
        botones.add(btnSalir);

        JPanel inferior = new JPanel(
                new BorderLayout(0, 10)
        );

        inferior.add(lblEstado, BorderLayout.NORTH);
        inferior.add(botones, BorderLayout.SOUTH);

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(campos, BorderLayout.CENTER);
        contenido.add(inferior, BorderLayout.SOUTH);

        setContentPane(contenido);
    }

    private void agregarFila(JPanel panel, int fila,
            JLabel etiqueta, JComponent campo) {

        GridBagConstraints posicion = new GridBagConstraints();

        posicion.gridx = 0;
        posicion.gridy = fila;

        posicion.anchor = GridBagConstraints.LINE_END;
        posicion.insets = new Insets(6, 0, 6, 12);

        panel.add(etiqueta, posicion);

        posicion.gridx = 1;
        posicion.weightx = 1;

        posicion.fill = GridBagConstraints.HORIZONTAL;
        posicion.insets = new Insets(6, 0, 6, 0);

        panel.add(campo, posicion);
    }

    private void configurarModo() {

        boolean crearCuenta = !acceso.tienePropietario();

        lblIndicacion.setText(
                crearCuenta
                        ? "Crea la cuenta del propietario"
                        : "Inicia sesión para continuar"
        );

        btnAcceder.setText(
                crearCuenta
                        ? "Crear cuenta"
                        : "Iniciar sesión"
        );

        lblConfirmacion.setVisible(crearCuenta);
        pwdConfirmacion.setVisible(crearCuenta);

        revalidate();
        repaint();
    }

    private void actualizarEstado() {

        txtUsuario.setEnabled(!ocupado);
        pwdClave.setEnabled(!ocupado);
        pwdConfirmacion.setEnabled(!ocupado);
        btnSalir.setEnabled(!ocupado);

        if (ocupado) {

            btnAcceder.setEnabled(false);

            lblEstado.setText(
                    "Procesando, espera un momento..."
            );

            return;
        }

        long segundos = acceso.getSegundosDeBloqueoRestantes();

        btnAcceder.setEnabled(segundos == 0);

        if (!acceso.tienePropietario()) {

            lblEstado.setText(
                    "La contraseña debe tener al menos 8 caracteres."
            );

        } else if (segundos > 0) {

            lblEstado.setText(
                    "Acceso bloqueado. Espera "
                    + segundos + " segundos."
            );

        } else {

            lblEstado.setText(
                    "Intentos disponibles: "
                    + acceso.getIntentosRestantes()
            );
        }
    }

    private void procesarAcceso() {

        if (ocupado) {
            return;
        }

        final String nombre = txtUsuario.getText().trim();
        final char[] clave = pwdClave.getPassword();

        char[] confirmacion = pwdConfirmacion.getPassword();

        final boolean crearCuenta = !acceso.tienePropietario();

        String error = null;

        if (nombre.isEmpty() || clave.length == 0) {

            error = "Debes ingresar el usuario y la contraseña.";

        } else if (crearCuenta
                && !Arrays.equals(clave, confirmacion)) {

            error = "Las contraseñas no coinciden.";
        }

        Arrays.fill(confirmacion, '\0');

        if (error != null) {

            Arrays.fill(clave, '\0');
            mostrarError(error);

            return;
        }

        ocupado = true;
        actualizarEstado();

        // Procesa la contraseña sin congelar la ventana.
        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {

                try {

                    if (crearCuenta) {

                        acceso.crearPropietario(nombre, clave);

                        return true;
                    }

                    return acceso.autenticar(nombre, clave);

                } finally {

                    Arrays.fill(clave, '\0');
                }
            }

            @Override
            protected void done() {

                try {

                    boolean correcto = get();

                    if (crearCuenta) {

                        configurarModo();

                        JOptionPane.showMessageDialog(
                                DialogoAcceso.this,
                                "Cuenta creada correctamente. "
                                + "Ahora inicia sesión."
                        );

                    } else if (correcto) {

                        dispose();

                    } else {

                        mostrarError(
                                "Usuario o contraseña incorrectos."
                        );
                    }

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();

                    mostrarError(
                            "La operación fue interrumpida."
                    );

                } catch (ExecutionException ex) {

                    Throwable causa = ex.getCause();

                    mostrarError(
                            causa == null
                                    ? "No se pudo completar la operación."
                                    : causa.getMessage()
                    );

                } finally {

                    ocupado = false;

                    pwdClave.setText("");
                    pwdConfirmacion.setText("");

                    actualizarEstado();
                }
            }

        }.execute();
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Acceso a FILTEX",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void cerrar() {

        if (!ocupado) {
            dispose();
        }
    }

    @Override
    public void dispose() {

        if (reloj != null) {
            reloj.stop();
        }

        super.dispose();
    }
}
