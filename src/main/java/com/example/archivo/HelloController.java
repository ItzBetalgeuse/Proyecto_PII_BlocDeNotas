package com.example.archivo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

public class HelloController {

    @FXML private TextArea AreaDelTexto;
    @FXML private AnchorPane ContenedorTexto;
    @FXML private TabPane Pestanas;
    private File PestanaActual;

    @FXML private void Nuevo() {
        Tab NuevaPestana = new Tab("Nuevo Archivo");
        TextArea nuevoTextArea = new TextArea();
        NuevaPestana.setContent(nuevoTextArea);
        Pestanas.getTabs().add(NuevaPestana);
        Pestanas.getSelectionModel().select(NuevaPestana);
    }

    @FXML private void Abrir() {
        FileChooser SeleccionDeTxt = new FileChooser();
        SeleccionDeTxt.setTitle("Abrir Archivo");
        SeleccionDeTxt.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        File Archivo = SeleccionDeTxt.showOpenDialog(null);
        if (Archivo != null) {
            try {
                StringBuilder Contenido = new StringBuilder();
                BufferedReader Lector = new BufferedReader(new FileReader(Archivo));
                String linea;
                while ((linea = Lector.readLine()) != null) {
                    Contenido.append(linea).append("\n");
                }
                Lector.close();

                if(Pestanas.getTabs().isEmpty()){
                    Tab nuevaPestana = new Tab(Archivo.getName());
                    TextArea nuevoTextArea = new TextArea(Contenido.toString());
                    nuevaPestana.setContent(nuevoTextArea);
                    Pestanas.getTabs().add(nuevaPestana);
                }else {
                    Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
                    if (PestanaActual != null && PestanaActual.getContent() instanceof TextArea areaDeTexto) {
                        areaDeTexto.setText(Contenido.toString());
                        PestanaActual.setText(Archivo.getName());
                    }
                }
            } catch (IOException e) {
                mostrarAlerta("Error al abrir el archivo", e.getMessage());
            }
        }
    }

    @FXML private void Guardar() {
        Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
        if ((PestanaActual != null) && (PestanaActual.getContent() instanceof TextArea areaDeTexto)) {
            File ArchivoAGuardar = this.PestanaActual;
            if (ArchivoAGuardar == null) {
                FileChooser SeleccionArchivo = new FileChooser();
                SeleccionArchivo.setTitle("Guardar Archivo");
                SeleccionArchivo.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
                ArchivoAGuardar = SeleccionArchivo.showSaveDialog(null);
                if (ArchivoAGuardar == null) {
                    return; // Cancelado por el usuario
                }
                PestanaActual.setText(ArchivoAGuardar.getName());
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(ArchivoAGuardar))) {
                writer.println(areaDeTexto.getText());
                this.PestanaActual = ArchivoAGuardar;
            } catch (IOException e) {
                mostrarAlerta("Error al guardar el archivo", e.getMessage());
            }
        }
    }
    @FXML private void Imprimir() {
        // Obtener la pestaña actual
        Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
        if (PestanaActual != null && PestanaActual.getContent() instanceof TextArea areaDeTexto) {

            // Verificar que haya una impresora seleccionada por defecto
            if (Printer.getDefaultPrinter() == null) {
                new Alert(Alert.AlertType.ERROR, "No hay una impresora seleccionada por defecto.").showAndWait();
                return;
            }

            // Crear un trabajo de impresión
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null) {
                printerJob.showPageSetupDialog(ContenedorTexto.getScene().getWindow());
                boolean success = printerJob.printPage(areaDeTexto);
                if (success) {
                    printerJob.endJob();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Hubo un error al imprimir, intente otra vez.").showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Hubo un error al iniciar el proceso de impresión").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "No hay un área de texto para imprimir.").showAndWait();
        }
    }

    @FXML private void Cerrar() {
        // Obtener la pestaña actual
        Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
        if (PestanaActual != null && PestanaActual.getContent() instanceof TextArea areaDeTexto) {

            // Verificar si el área de texto no está vacía
            if (!areaDeTexto.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmación");
                alert.setHeaderText(null);
                alert.setContentText("¿Estás seguro de que deseas cerrar la pestaña sin guardar?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Cerrar la pestaña
                    Pestanas.getTabs().remove(PestanaActual);
                }
            } else {
                // Si el área de texto está vacía, simplemente borrar la pestaña
                Pestanas.getTabs().remove(PestanaActual);
            }
        }
    }

    @FXML private void Salir() { System.exit(0); }

    private void mostrarAlerta(String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML private void Cortar(){
        try {
            Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
            AreaDelTexto = (TextArea) PestanaActual.getContent();
            //Verifica si hay un texto seleccionado
            if (AreaDelTexto.getSelectedText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Seleccione el texto a cortar.").showAndWait();
                AreaDelTexto.requestFocus();
                return;
            }
            //Utiliza el portapapeles del sistema para guardar el texto
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            String segment = AreaDelTexto.getSelectedText();
            content.putString(segment);
            clipboard.setContent(content);
            //Indexa el segmento de texto cortado para poder manipularlo
            IndexRange range = AreaDelTexto.getSelection();
            String original_text = AreaDelTexto.getText();
            String first_part = original_text.substring(0, range.getStart());
            String last_part = original_text.substring(range.getEnd());
            AreaDelTexto.setText(first_part + last_part);
        }catch (NullPointerException ignored){}
    }

    @FXML private void Copiar(){
        try {
            Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
            AreaDelTexto = (TextArea) PestanaActual.getContent();
            //Verifica si hay un texto seleccionado
            if (AreaDelTexto.getSelectedText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Seleccione el texto para copiar.").showAndWait();
                AreaDelTexto.requestFocus();
                return;
            }
            //Utiliza el portapapeles del sistema para guardar el contenido copiado
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String text = AreaDelTexto.getSelectedText();
            ClipboardContent content = new ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);
        }catch (NullPointerException ignored){}
    }

    @FXML private void Pegar(){
        try {
            Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
            AreaDelTexto = (TextArea) PestanaActual.getContent();
            //Verifica si hay texto copiado
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (!clipboard.hasContent(DataFormat.PLAIN_TEXT) || clipboard.getString().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Copie el texto.").showAndWait();
                return;
            }
            //Obtiene el texto del portapapeles para pegarlo en el TextField
            String addText = clipboard.getString();
            IndexRange range = AreaDelTexto.getSelection();
            String originalText = AreaDelTexto.getText();
            String firstPart = originalText.substring(0, range.getStart());
            String lastPart = originalText.substring(range.getEnd());
            AreaDelTexto.setText(firstPart + addText + lastPart);
        }catch (NullPointerException ignored){}
    }

    @FXML private void Buscar(){
        // Obtener la pestaña actual
        Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
        if (PestanaActual != null && PestanaActual.getContent() instanceof TextArea areaDeTexto) {

            // Mostrar cuadro de diálogo para ingresar la palabra a buscar
            TextInputDialog buscarDialog = new TextInputDialog();
            buscarDialog.setTitle("Buscar");
            buscarDialog.setHeaderText("Ingrese la palabra a buscar");
            buscarDialog.setContentText("Palabra a buscar:");

            Optional<String> buscarResult = buscarDialog.showAndWait();
            if (buscarResult.isPresent()) {
                String buscar = buscarResult.get();

                // Realizar la búsqueda en el texto del área de texto
                String texto = areaDeTexto.getText();
                int index = texto.indexOf(buscar);
                if (index != -1) {
                    // Se encontró la palabra, seleccionarla en el área de texto
                    areaDeTexto.selectRange(index, index + buscar.length());
                } else {
                    // No se encontró la palabra, mostrar un mensaje
                    new Alert(Alert.AlertType.INFORMATION, "La palabra no se encontró.").showAndWait();
                }
            }
        }
    }

    @FXML private void AcercaDe(){
        //Abre la ventana "Acerca de"
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AcercaDe.fxml"));
            Parent AcercaDe = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(AcercaDe, 320, 200));
            stage.setTitle("Acerca de");
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icon.png"))));
            stage.setResizable(false);
            stage.show();
        }catch (Exception ignored){}
    }

    @FXML private void Manual(){
        //Abre la ventana del Manual
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Manual.fxml"));
            Parent manual = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(manual, 600, 454));
            stage.setTitle("Manual");
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icon.png"))));
            stage.setResizable(false);
            stage.show();
        }catch (Exception ignored){}
    }
    @FXML private void BuscarYRemplazar() {
        // Obtener la pestaña actual
        Tab PestanaActual = Pestanas.getSelectionModel().getSelectedItem();
        if (PestanaActual != null && PestanaActual.getContent() instanceof TextArea areaDeTexto) {

            // Mostrar cuadro de diálogo para ingresar la palabra a buscar
            TextInputDialog buscarDialog = new TextInputDialog();
            buscarDialog.setTitle("Buscar y Reemplazar");
            buscarDialog.setHeaderText("Ingrese la palabra a buscar");
            buscarDialog.setContentText("Palabra a buscar:");

            Optional<String> buscarResult = buscarDialog.showAndWait();
            if (buscarResult.isPresent()) {
                String buscar = buscarResult.get();

                // Mostrar cuadro de diálogo para ingresar la palabra de reemplazo
                TextInputDialog reemplazarDialog = new TextInputDialog();
                reemplazarDialog.setTitle("Buscar y Reemplazar");
                reemplazarDialog.setHeaderText("Ingrese la palabra de reemplazo");
                reemplazarDialog.setContentText("Palabra de reemplazo:");

                Optional<String> resultadoReemplazar = reemplazarDialog.showAndWait();
                if (resultadoReemplazar.isPresent()) {
                    String reemplazar = resultadoReemplazar.get();

                    // Realizar la búsqueda y el reemplazo en el texto del área de texto
                    try {
                        String texto = areaDeTexto.getText();
                        if (texto.contains(buscar)) {
                            texto = texto.replaceAll(buscar, reemplazar);
                            areaDeTexto.setText(texto);
                        } else {
                            // Mostrar mensaje de error si la palabra buscada no se encuentra
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("La palabra buscada no se encontró en el texto.");
                            alert.showAndWait();
                        }
                    }catch (PatternSyntaxException e){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Esta funcion solo reemplaza palabras, no símbolos.");
                        alert.showAndWait();
                    }
                }
            }
        }
    }
}