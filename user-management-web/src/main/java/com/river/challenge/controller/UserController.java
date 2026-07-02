package com.river.challenge.controller;

import com.river.challenge.model.User;
import com.river.challenge.service.UserService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

/**
 * Controlador (MVC) de la vista de gestión de usuarios.
 * Se apoya exclusivamente en la fachada {@link UserService} de la librería.
 */
public class UserController extends SelectorComposer<Component> {

    private static final String PASSWORD_HINT = "••••••••";
    private static final String PASSWORD_EDIT_HINT = "Dejar vacío para no cambiarla";

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

    // Punto de entrada único a la lógica de la librería (JAR core)
    private final UserService userService = new UserService();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        refreshUsersList();
    }

    private void refreshUsersList() {
        usersGrid.getItems().clear();
        List<User> users = userService.listActiveUsers();

        for (final User user : users) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(String.valueOf(user.getId())));
            item.appendChild(new Listcell(user.getUsername()));
            item.appendChild(new Listcell(user.getEmail()));

            Listcell actionCell = new Listcell();
            Hlayout actionsLayout = new Hlayout();
            actionsLayout.setSclass("row-actions");
            actionsLayout.setSpacing("8px");

            Button editBtn = new Button("Editar");
            editBtn.setSclass("btn-ghost btn-sm");
            editBtn.addEventListener("onClick", event -> loadUserForEdit(user));

            Button deleteBtn = new Button("Eliminar");
            deleteBtn.setSclass("btn-danger btn-sm");
            deleteBtn.addEventListener("onClick", event -> confirmDelete(user));

            actionsLayout.appendChild(editBtn);
            actionsLayout.appendChild(deleteBtn);
            actionCell.appendChild(actionsLayout);
            item.appendChild(actionCell);

            usersGrid.appendChild(item);
        }
    }

    private void loadUserForEdit(User user) {
        idInput.setValue(user.getId());
        usernameInput.setValue(user.getUsername());
        emailInput.setValue(user.getEmail());
        // No se carga la contraseña (está cifrada). Vacío = no cambiarla.
        passwordInput.setValue("");
        passwordInput.setPlaceholder(PASSWORD_EDIT_HINT);
        saveBtn.setLabel("Actualizar Usuario");
    }

    private void confirmDelete(User user) {
        Messagebox.show("¿Está seguro de eliminar a este usuario?", "Confirmación",
            Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, evt -> {
                if (Messagebox.ON_OK.equals(evt.getName())) {
                    if (userService.deleteUser(user.getId())) {
                        Messagebox.show("Usuario eliminado con éxito", "Información",
                                Messagebox.OK, Messagebox.INFORMATION);
                        refreshUsersList();
                        clearFields();
                    }
                }
            });
    }

    @Listen("onClick = #saveBtn")
    public void onSaveUser() {
        String username = usernameInput.getValue();
        String email = emailInput.getValue();
        String password = passwordInput.getValue();

        Integer id = idInput.getValue();
        boolean isNew = (id == null);
        boolean passwordProvided = password != null && !password.trim().isEmpty();

        // Al crear la contraseña es obligatoria; al editar es opcional
        String validationError = userService.validate(username, email, password, isNew);
        if (validationError != null) {
            Messagebox.show(validationError, "Error de Validación", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        boolean success;
        if (isNew) {
            success = userService.createUser(new User(null, username, email, password));
        } else {
            success = userService.updateUser(new User(id, username, email, password), passwordProvided);
        }

        if (success) {
            Messagebox.show("Operación realizada con éxito", "Éxito", Messagebox.OK, Messagebox.INFORMATION);
            refreshUsersList();
            clearFields();
        } else {
            Messagebox.show("Ocurrió un error al procesar en la Base de Datos. Verifica duplicados.",
                    "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Listen("onClick = #clearBtn")
    public void clearFields() {
        idInput.setValue(null);
        usernameInput.setValue("");
        emailInput.setValue("");
        passwordInput.setValue("");
        passwordInput.setPlaceholder(PASSWORD_HINT);
        saveBtn.setLabel("Guardar Usuario");
    }
}
