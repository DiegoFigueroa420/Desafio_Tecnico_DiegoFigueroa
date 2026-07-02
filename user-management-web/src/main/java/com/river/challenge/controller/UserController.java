package com.river.challenge.controller;

import com.river.challenge.auth.UserAuthenticatorImpl;
import com.river.challenge.dao.UserDao;
import com.river.challenge.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class UserController extends SelectorComposer<Component> {

    // Vincular automáticamente los componentes del .zul mediante su ID
    @Wire
    private Intbox idInput;
    @Wire
    private Textbox usernameInput;
    @Wire
    private Textbox emailInput;
    @Wire
    private Textbox passwordInput;
    @Wire
    private Listbox usersGrid;
    @Wire
    private Button saveBtn;

    // Instanciar los servicios de la biblioteca JAR
    private final UserDao userDao = new UserDao();
    private final UserAuthenticatorImpl authenticator = new UserAuthenticatorImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        // Al cargar la página, listamos inmediatamente los usuarios de la BD
        refreshUsersList();
    }

    // Método para renderizar y refrescar la tabla
    private void refreshUsersList() {
        usersGrid.getItems().clear(); // Limpiar filas anteriores
        List<User> users = userDao.findAll(); // Llamada al JAR del Core

        for (final User user : users) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(String.valueOf(user.getId())));
            item.appendChild(new Listcell(user.getUsername()));
            item.appendChild(new Listcell(user.getEmail()));

            // Celda de acciones (Modifica y Borra)
            Listcell actionCell = new Listcell();
            Hlayout actionsLayout = new Hlayout();
            actionsLayout.setSpacing("10px");

            // Botón Seleccionar/Editar
            Button editBtn = new Button("Editar");
            editBtn.setMold("trendy");
            editBtn.addEventListener("onClick", event -> {
                idInput.setValue(user.getId());
                usernameInput.setValue(user.getUsername());
                emailInput.setValue(user.getEmail());
                passwordInput.setValue(user.getPassword());
                saveBtn.setLabel("Actualizar Usuario");
            });

            // Botón Eliminar
            Button deleteBtn = new Button("Eliminar");
            deleteBtn.setMold("trendy");
            deleteBtn.setStyle("background-color: #d9534f; color: white;");
            deleteBtn.addEventListener("onClick", event -> {
                Messagebox.show("¿Está seguro de eliminar a este usuario?", "Confirmación",
                    Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, evt -> {
                        if (Messagebox.ON_OK.equals(evt.getName())) {
                            boolean deleted = userDao.delete(user.getId()); // Llamada al JAR del Core
                            if (deleted) {
                                Messagebox.show("Usuario eliminado con éxito", "Información", Messagebox.OK, Messagebox.INFORMATION);
                                refreshUsersList();
                                clearFields();
                            }
                        }
                    });
            });

            actionsLayout.appendChild(editBtn);
            actionsLayout.appendChild(deleteBtn);
            actionCell.appendChild(actionsLayout);
            item.appendChild(actionCell);

            usersGrid.appendChild(item);
        }
    }

    //el click del boton de Guardar/Actualiza
    @Listen("onClick = #saveBtn")
    public void onSaveUser() {
        String username = usernameInput.getValue();
        String email = emailInput.getValue();
        String password = passwordInput.getValue();

        // Validar lógica de negocio usando el servicio del JAR
        String validationError = authenticator.validateUserForm(username, email, password);
        if (validationError != null) {
            Messagebox.show(validationError, "Error de Validación", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        Integer id = idInput.getValue();
        boolean success;

        if (id == null) {
            // Crear nuevo usuario
            User newUser = new User(null, username, email, password);
            success = userDao.create(newUser);
        } else {
            // Modificar usuario existente
            User existingUser = new User(id, username, email, password);
            success = userDao.update(existingUser);
        }

        if (success) {
            Messagebox.show("Operación realizada con éxito", "Éxito", Messagebox.OK, Messagebox.INFORMATION);
            refreshUsersList();
            clearFields();
        } else {
            Messagebox.show("Ocurrió un error al procesar en la Base de Datos. Verifica duplicados.", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    //elclic del botón de Limpiar
    @Listen("onClick = #clearBtn")
    public void clearFields() {
        idInput.setValue(null);
        usernameInput.setValue("");
        emailInput.setValue("");
        passwordInput.setValue("");
        saveBtn.setLabel("Guardar Usuario");
    }
}
