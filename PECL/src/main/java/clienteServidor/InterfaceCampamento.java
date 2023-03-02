package clienteServidor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceCampamento extends Remote {
    String getNumActividades(String idNino) throws RemoteException;
    String getNumNinosMerendando() throws RemoteException;
    String getNumBandejasSucias() throws RemoteException;
    String getNumBandejasLimpias() throws RemoteException;
    String getNumNinosEsperandoTirolina() throws RemoteException;
    String getUsosTirolina() throws RemoteException;
    String getNumNinosColaEsperaSoga() throws RemoteException;
}