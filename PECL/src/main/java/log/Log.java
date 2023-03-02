package log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    //Atributos de la clase log
    private final  String ruta;
    private final  File file;
    private final boolean debug;

    //Métodos de la clase log

    //Método constructor
    public Log(boolean debug) {
        this.debug = debug;
        ruta = "debug.log";
        this.file = new File(ruta);
        //Creamos el archivo log
        if(debug) {
            crearLog();
        }
    }

    //Metodo para crear el archivo log
    public void crearLog(){
        try {
            String contenido = "[ARCHIVO LOG CAMPAMENTO]\n\n";

            if(file.exists()){
                //Si existe lo borramos y creamos uno nuevo al inicio de la ejecucion del programa
                if(file.delete()){
                    file.createNewFile();
                }
            } else {
                file.createNewFile();
            }
            //Mostramos la ruta absoluta del fichero
            System.out.println("[LOG] Archivo creado en: " + file.getAbsolutePath());

            FileWriter escribir = new FileWriter(file);
            try (BufferedWriter bw = new BufferedWriter(escribir)) {
                bw.write(contenido);
            }
        } catch(IOException ex) {
            System.out.println("ERROR: " + ex);
        }
    } // Cierre del método

    //Metodo para escribir en el log en caso de que la variable debug este activada
    public synchronized void escribirEnLog(String contenido) {
        if(debug) {
            Date fecha = new Date();
            DateFormat formatoFecha = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]");
            try {
                FileWriter escribir = new FileWriter(file, true);
                try (BufferedWriter bw = new BufferedWriter(escribir)) {
                    bw.write( formatoFecha.format(fecha) + " --> " + contenido + "\n" );
                }
            } catch(IOException ex) {
                System.out.println("ERROR: " + ex);
            }
        }
    }
}
