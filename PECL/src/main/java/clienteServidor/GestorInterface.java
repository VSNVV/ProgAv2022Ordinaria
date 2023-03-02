package clienteServidor;

import concurrencia.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GestorInterface extends UnicastRemoteObject implements InterfaceCampamento {
    private Campamento campamento;
    private Merendero merendero;
    private Tirolina tirolina;
    private Soga soga;
    public GestorInterface(Campamento _campamento, Merendero _merendero, Tirolina _tirolina, Soga _soga) throws RemoteException{
        this.campamento = _campamento;
        this.merendero = _merendero;
        this.tirolina = _tirolina;
        this.soga = _soga;
    }
    public String getNumActividades(String idNino){
        Integer participaciones = null;
        for(int i = 0; i < (getCampamento().getAforoCampamento().size()); i++){
            Niño ninoActual = getCampamento().getAforoCampamento().get(i);
            String ninoActualId = ninoActual.getID();
            if (ninoActualId.equals(idNino)){
                //Es el niño que buscamos
                participaciones = ninoActual.getParticipaciones();
           }
        }
        String result = "";
        if (participaciones == null){
            result = "No encontrado";
        }
        else {
            result = participaciones.toString();
        }
        return result;
    }
    public String getNumNinosMerendando(){
        String respuesta = "";
        Integer numero = getMerendero().getNumNinosMerendando();
        respuesta = numero.toString();
        return respuesta;
    }
    public String getNumBandejasSucias(){
        String respuesta = "";
        Integer numero = getMerendero().getListaBandejasSucias().size();
        respuesta = numero.toString();
        return respuesta;
    }
    public String getNumBandejasLimpias(){
        String respuesta = "";
        Integer numero = getMerendero().getListaBandejasLimpias().size();
        respuesta = numero.toString();
        return respuesta;
    }
    public String getNumNinosEsperandoTirolina(){
        String respuesta = "";
        Integer numero = getTirolina().getNumNinosEsperandoTirolina();
        respuesta = numero.toString();
        return respuesta;
    }
    public String getUsosTirolina(){
        String respuesta = "";
        Integer numero = getTirolina().getNumUsosTirolina();
        respuesta = numero.toString();
        return respuesta;
    }
    public String getNumNinosColaEsperaSoga(){
        String respuesta = "";
        Integer numero = getSoga().getNumNinosEntradaSoga();
        respuesta = numero.toString();
        return respuesta;
    }

    public Campamento getCampamento() {
        return campamento;
    }
    public Merendero getMerendero(){
        return merendero;
    }
    public Tirolina getTirolina(){
        return tirolina;
    }
    public Soga getSoga(){
        return soga;
    }
}