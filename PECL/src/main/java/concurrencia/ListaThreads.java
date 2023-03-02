package concurrencia;
import java.util.*;
import javax.swing.JTextField;

/* La clase ListaThreads permite gestionar las listas de threads en los monitores,
con métodos para meter y sacar threads en ella. Cada vez que una lista se modifica,
se imprime su nuevo contenido en el JTextField que toma como parámetro el constructor. */
public class ListaThreads
{
    ArrayList<Niño> listaNinos;
    ArrayList<Monitor> listaMonitores;
    String numBandeja;
    JTextField tf;
    
    public ListaThreads(JTextField tf)
    {
        listaNinos = new ArrayList<Niño>();
        listaMonitores = new ArrayList<Monitor>();
        this.tf=tf;
    }
    
    public synchronized void meterNino(Niño nino)
    {
        listaNinos.add(nino);
        imprimirNino();
    }

    public synchronized void meterMonitor(Monitor monitor)
    {
        listaMonitores.add(monitor);
        imprimirMonitor();
    }
    
    public synchronized void sacarNino(Niño nino)
    {
        listaNinos.remove(nino);
        imprimirNino();
    }

    public synchronized void sacarMonitor(Monitor monitor)
    {
        listaMonitores.remove(monitor);
        imprimirMonitor();
    }

    public synchronized void ponerNumeroSucias(int numero){
        setNumBandeja(numero);
        imprimirNumero();
    }

    public synchronized void ponerNumeroLimpias(int numero){
        setNumBandeja(numero);
        imprimirNumero();
    }


    public void imprimirNino()
    {
        String contenido="";
        for(int i=0; i<listaNinos.size(); i++)
        {
            contenido=contenido+listaNinos.get(i).getID()+" ";
        }
        tf.setText(contenido);
    }

    public void imprimirMonitor()
    {
        String contenido="";
        for(int i=0; i<listaMonitores.size(); i++)
        {
            contenido=contenido+listaMonitores.get(i).getID()+" ";
        }
        tf.setText(contenido);
    }
    public void imprimirNumero(){
        String contenido = "";
        contenido = numBandeja;
        tf.setText(contenido);
    }
    public void setNumBandeja(int num){
        Integer numero = num;
        this.numBandeja = numero.toString();
    }
}
