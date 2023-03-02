/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencia;

import clienteServidor.GestorInterface;
import log.Log;

import javax.swing.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Formatter;

/**
 *
 * @author vsnv
 */
public class ProgPrincipal extends javax.swing.JFrame {
    private Campamento campamentoServidor;
    private Soga sogaServidor;
    private Tirolina tirolinaServidor;
    private Merendero merenderoServidor;
    private ZonaComun zonaComunServidor;
    private Log archivoLog;
    private Paso paso;
    private boolean botonPulsado = false;

    /**
     * Creates new form ProgPrincipal
     */
    public ProgPrincipal() {
        initComponents();
        this.archivoLog = new Log(true);
        this.campamentoServidor = new Campamento(getjTextFieldPuerta1(), getjTextFieldPuerta2(), getArchivoLog());
        this.sogaServidor = new Soga(getjTextFieldListaEsperaSoga(), getjTextFieldMonitorSoga(), getjTextFieldEquipoASoga(), getjTextFieldEquipoBSoga(), getArchivoLog());
        this.tirolinaServidor = new Tirolina(getjTextFieldListaEsperaTirolina(), getjTextFieldMonitorTirolina(), getjTextFieldPreparacionTirolina(), getjTextFieldTirandoTirolina(), getjTextFieldAcabandoTirolina(), getArchivoLog());
        this.merenderoServidor = new Merendero(getjTextFieldListaEsperaMerendero(), getjTextFieldListaBandejasLimpiasMerendero(), getjTextFieldListaBandejasSuciasMerendero(), getjTextFieldListaMonitoresMerendero(), getjTextFieldListaNinosMerendero(), getArchivoLog());
        this.zonaComunServidor = new ZonaComun(getjTextFieldMonitoresZonaComun(), getjTextFieldNinosZonaComun(), getArchivoLog());
        this.paso = new Paso();
        //Creamos a los monitores
        for (int i = 0; i < 4; i++) {
            String nombre = "M" + i;
            Monitor monitor = new Monitor(nombre, getCampamentoServidor(), getSogaServidor(), getMerenderoServidor(), getTirolinaServidor(), getZonaComunServidor(), getPaso());
            monitor.start();
            nombre = null;
        }
        //Creamos a los niños
        for (int i = 0; i < 240; i++){
            //Bucle para crear los 20k hilos paulatinamente (problema de los ids)
            Formatter fmt = new Formatter();
            fmt.format("%05d",i);
            String nombre = "N" + fmt;
            Niño nino = new Niño(nombre, getCampamentoServidor(), getSogaServidor(), getMerenderoServidor(), getTirolinaServidor(), getZonaComunServidor(), getPaso());
            nino.start();
            nombre = null;
        }
        //Inicializamos conexion
        try
        {
            GestorInterface obj = new GestorInterface(getCampamentoServidor(), getMerenderoServidor(), getTirolinaServidor(), getSogaServidor());
            Registry registry = LocateRegistry.createRegistry(1099);
            Naming.rebind("//localhost/ObjetoCampamento",obj);
            System.out.println("El Objeto Campamento ha quedado registrado");
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEntradaCampamento = new javax.swing.JPanel();
        labelEntradaCampamento = new javax.swing.JLabel();
        panelPuerta1 = new javax.swing.JPanel();
        labelPuerta1 = new javax.swing.JLabel();
        jTextFieldPuerta1 = new javax.swing.JTextField();
        panelPuerta2 = new javax.swing.JPanel();
        labelPuerta2 = new javax.swing.JLabel();
        jTextFieldPuerta2 = new javax.swing.JTextField();
        panelCampamento = new javax.swing.JPanel();
        labelCampamento = new javax.swing.JLabel();
        panelSoga = new javax.swing.JPanel();
        labelSoga = new javax.swing.JLabel();
        labelMonitorSoga = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldListaEsperaSoga = new javax.swing.JTextField();
        jTextFieldMonitorSoga = new javax.swing.JTextField();
        jTextFieldEquipoASoga = new javax.swing.JTextField();
        jTextFieldEquipoBSoga = new javax.swing.JTextField();
        panelTirolina = new javax.swing.JPanel();
        labelTirolina = new javax.swing.JLabel();
        labelMonitorTirolina = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelTirandoTirolina = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldListaEsperaTirolina = new javax.swing.JTextField();
        jTextFieldMonitorTirolina = new javax.swing.JTextField();
        jTextFieldPreparacionTirolina = new javax.swing.JTextField();
        jTextFieldTirandoTirolina = new javax.swing.JTextField();
        jTextFieldAcabandoTirolina = new javax.swing.JTextField();
        panelZonaComun = new javax.swing.JPanel();
        labelZonaComun = new javax.swing.JLabel();
        labelMonitoresZonaComun = new javax.swing.JLabel();
        labelNinosZonaComún = new javax.swing.JLabel();
        jTextFieldMonitoresZonaComun = new javax.swing.JTextField();
        jTextFieldNinosZonaComun = new javax.swing.JTextField();
        panelMerendero = new javax.swing.JPanel();
        labelMerendero = new javax.swing.JLabel();
        labelBandejasSuciasMerendero = new javax.swing.JLabel();
        labelMonitoresMerendero = new javax.swing.JLabel();
        labelBandejasLimpias = new javax.swing.JLabel();
        jTextFieldListaEsperaMerendero = new javax.swing.JTextField();
        jTextFieldListaBandejasSuciasMerendero = new javax.swing.JTextField();
        jTextFieldListaMonitoresMerendero = new javax.swing.JTextField();
        jTextFieldListaNinosMerendero = new javax.swing.JTextField();
        jTextFieldListaBandejasLimpiasMerendero = new javax.swing.JTextField();
        botonPararReanudar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 153, 153));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelEntradaCampamento.setBackground(new java.awt.Color(255, 255, 255));
        panelEntradaCampamento.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEntradaCampamento.setForeground(new java.awt.Color(0, 0, 0));
        labelEntradaCampamento.setText("ENTRADA AL CAMPAMENTO");
        panelEntradaCampamento.add(labelEntradaCampamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, -1));

        panelPuerta1.setBackground(new java.awt.Color(153, 153, 153));
        panelPuerta1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelPuerta1.setForeground(new java.awt.Color(0, 0, 0));
        labelPuerta1.setText("PUERTA 1");
        panelPuerta1.add(labelPuerta1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, -1, -1));

        jTextFieldPuerta1.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldPuerta1.setForeground(new java.awt.Color(0, 0, 0));
        jTextFieldPuerta1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPuerta1ActionPerformed(evt);
            }
        });
        panelPuerta1.add(jTextFieldPuerta1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 740, 60));

        panelEntradaCampamento.add(panelPuerta1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 760, 110));

        panelPuerta2.setBackground(new java.awt.Color(153, 153, 153));
        panelPuerta2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelPuerta2.setForeground(new java.awt.Color(0, 0, 0));
        labelPuerta2.setText("PUERTA 2");
        panelPuerta2.add(labelPuerta2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

        jTextFieldPuerta2.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldPuerta2.setForeground(new java.awt.Color(0, 0, 0));
        panelPuerta2.add(jTextFieldPuerta2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 660, 60));

        panelEntradaCampamento.add(panelPuerta2, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 30, 680, 110));

        getContentPane().add(panelEntradaCampamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1510, 150));

        panelCampamento.setBackground(new java.awt.Color(255, 255, 255));
        panelCampamento.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelCampamento.setForeground(new java.awt.Color(0, 0, 0));
        labelCampamento.setText("CAMPAMENTO");
        panelCampamento.add(labelCampamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 10, -1, -1));

        panelSoga.setBackground(new java.awt.Color(153, 153, 153));
        panelSoga.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelSoga.setForeground(new java.awt.Color(0, 0, 0));
        labelSoga.setText("SOGA");
        panelSoga.add(labelSoga, new org.netbeans.lib.awtextra.AbsoluteConstraints(343, 5, -1, -1));

        labelMonitorSoga.setForeground(new java.awt.Color(0, 0, 0));
        labelMonitorSoga.setText("Monitor");
        panelSoga.add(labelMonitorSoga, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Equipo A");
        panelSoga.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 80, -1, -1));

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Equipo B");
        panelSoga.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 80, -1, -1));

        jTextFieldListaEsperaSoga.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldListaEsperaSoga.setForeground(new java.awt.Color(0, 0, 0));
        panelSoga.add(jTextFieldListaEsperaSoga, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 700, 50));

        jTextFieldMonitorSoga.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldMonitorSoga.setForeground(new java.awt.Color(0, 0, 0));
        panelSoga.add(jTextFieldMonitorSoga, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 130, 40));

        jTextFieldEquipoASoga.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldEquipoASoga.setForeground(new java.awt.Color(0, 0, 0));
        panelSoga.add(jTextFieldEquipoASoga, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 250, 80));

        jTextFieldEquipoBSoga.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldEquipoBSoga.setForeground(new java.awt.Color(0, 0, 0));
        panelSoga.add(jTextFieldEquipoBSoga, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, 250, 80));

        panelCampamento.add(panelSoga, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 720, 190));

        panelTirolina.setBackground(new java.awt.Color(153, 153, 153));
        panelTirolina.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTirolina.setForeground(new java.awt.Color(0, 0, 0));
        labelTirolina.setText("TIROLINA");
        panelTirolina.add(labelTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, -1, -1));

        labelMonitorTirolina.setForeground(new java.awt.Color(0, 0, 0));
        labelMonitorTirolina.setText("Monitor");
        panelTirolina.add(labelMonitorTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, -1, -1));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Preparación");
        panelTirolina.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, -1, -1));

        jLabelTirandoTirolina.setForeground(new java.awt.Color(0, 0, 0));
        jLabelTirandoTirolina.setText("Tirolina");
        panelTirolina.add(jLabelTirandoTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 80, -1, -1));

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Finalización");
        panelTirolina.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 80, -1, -1));

        jTextFieldListaEsperaTirolina.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldListaEsperaTirolina.setForeground(new java.awt.Color(0, 0, 0));
        panelTirolina.add(jTextFieldListaEsperaTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 690, 40));

        jTextFieldMonitorTirolina.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldMonitorTirolina.setForeground(new java.awt.Color(0, 0, 0));
        panelTirolina.add(jTextFieldMonitorTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 140, 40));

        jTextFieldPreparacionTirolina.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldPreparacionTirolina.setForeground(new java.awt.Color(0, 0, 0));
        panelTirolina.add(jTextFieldPreparacionTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 150, 40));

        jTextFieldTirandoTirolina.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldTirandoTirolina.setForeground(new java.awt.Color(0, 0, 0));
        panelTirolina.add(jTextFieldTirandoTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, 160, 40));

        jTextFieldAcabandoTirolina.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldAcabandoTirolina.setForeground(new java.awt.Color(0, 0, 0));
        panelTirolina.add(jTextFieldAcabandoTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 200, 40));

        panelCampamento.add(panelTirolina, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 30, 730, 190));

        panelZonaComun.setBackground(new java.awt.Color(153, 153, 153));
        panelZonaComun.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelZonaComun.setForeground(new java.awt.Color(0, 0, 0));
        labelZonaComun.setText("ZONA COMÚN");
        panelZonaComun.add(labelZonaComun, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, -1));

        labelMonitoresZonaComun.setForeground(new java.awt.Color(0, 0, 0));
        labelMonitoresZonaComun.setText("Monitores");
        panelZonaComun.add(labelMonitoresZonaComun, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, -1, -1));

        labelNinosZonaComún.setForeground(new java.awt.Color(0, 0, 0));
        labelNinosZonaComún.setText("Niños");
        panelZonaComun.add(labelNinosZonaComún, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 40, -1, -1));

        jTextFieldMonitoresZonaComun.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldMonitoresZonaComun.setForeground(new java.awt.Color(0, 0, 0));
        panelZonaComun.add(jTextFieldMonitoresZonaComun, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 330, 40));

        jTextFieldNinosZonaComun.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldNinosZonaComun.setForeground(new java.awt.Color(0, 0, 0));
        panelZonaComun.add(jTextFieldNinosZonaComun, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 1070, 50));

        panelCampamento.add(panelZonaComun, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 1470, 120));

        panelMerendero.setBackground(new java.awt.Color(153, 153, 153));
        panelMerendero.setForeground(new java.awt.Color(153, 153, 153));
        panelMerendero.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelMerendero.setForeground(new java.awt.Color(0, 0, 0));
        labelMerendero.setText("MERENDERO");
        panelMerendero.add(labelMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 10, -1, -1));

        labelBandejasSuciasMerendero.setForeground(new java.awt.Color(0, 0, 0));
        labelBandejasSuciasMerendero.setText("Bandejas sucias");
        panelMerendero.add(labelBandejasSuciasMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        labelMonitoresMerendero.setForeground(new java.awt.Color(0, 0, 0));
        labelMonitoresMerendero.setText("Monitores");
        panelMerendero.add(labelMonitoresMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 110, -1, -1));

        labelBandejasLimpias.setForeground(new java.awt.Color(0, 0, 0));
        labelBandejasLimpias.setText("Bandejas limpias");
        panelMerendero.add(labelBandejasLimpias, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, -1, -1));

        jTextFieldListaEsperaMerendero.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldListaEsperaMerendero.setForeground(new java.awt.Color(0, 0, 0));
        panelMerendero.add(jTextFieldListaEsperaMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 1430, 60));

        jTextFieldListaBandejasSuciasMerendero.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldListaBandejasSuciasMerendero.setForeground(new java.awt.Color(0, 0, 0));
        jTextFieldListaBandejasSuciasMerendero.setText("25");
        panelMerendero.add(jTextFieldListaBandejasSuciasMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 320, 40));

        jTextFieldListaMonitoresMerendero.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldListaMonitoresMerendero.setForeground(new java.awt.Color(0, 0, 0));
        panelMerendero.add(jTextFieldListaMonitoresMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 130, 320, 40));

        jTextFieldListaNinosMerendero.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldListaNinosMerendero.setForeground(new java.awt.Color(0, 0, 0));
        panelMerendero.add(jTextFieldListaNinosMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 110, 420, 60));

        jTextFieldListaBandejasLimpiasMerendero.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldListaBandejasLimpiasMerendero.setForeground(new java.awt.Color(0, 0, 0));
        panelMerendero.add(jTextFieldListaBandejasLimpiasMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 320, 40));

        panelCampamento.add(panelMerendero, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, 1460, 180));

        botonPararReanudar.setBackground(new java.awt.Color(255, 255, 255));
        botonPararReanudar.setText("Parar y Reanudar");
        botonPararReanudar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPararReanudarActionPerformed(evt);
            }
        });
        panelCampamento.add(botonPararReanudar, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 550, 150, 40));

        getContentPane().add(panelCampamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 1510, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPuerta1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPuerta1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPuerta1ActionPerformed

    private void botonPararReanudarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPararReanudarActionPerformed
        if(!isBotonPulsado()) //Si no se ha pulsado
        {
            setBotonPulsado(true); //Lo cambiamos a pulsado
            getBotonPararReanudar().setText("Reanudar"); //Cambiamos el texto
            getPaso().cerrar(); //Cerramos el paso para que los pintores se detengan
        }
        else //Si ya se había pulsado
        {
            setBotonPulsado(false); //Lo cambiamos
            getBotonPararReanudar().setText("Detener"); //Cambiamos el texto
            getPaso().abrir(); //Abrimos el paso para que los pintores sigan trabajando
        }
    }//GEN-LAST:event_botonPararReanudarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProgPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProgPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProgPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProgPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProgPrincipal().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonPararReanudar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelTirandoTirolina;
    private javax.swing.JTextField jTextFieldAcabandoTirolina;
    private javax.swing.JTextField jTextFieldEquipoASoga;
    private javax.swing.JTextField jTextFieldEquipoBSoga;
    private javax.swing.JTextField jTextFieldListaBandejasLimpiasMerendero;
    private javax.swing.JTextField jTextFieldListaBandejasSuciasMerendero;
    private javax.swing.JTextField jTextFieldListaEsperaMerendero;
    private javax.swing.JTextField jTextFieldListaEsperaSoga;
    private javax.swing.JTextField jTextFieldListaEsperaTirolina;
    private javax.swing.JTextField jTextFieldListaMonitoresMerendero;
    private javax.swing.JTextField jTextFieldListaNinosMerendero;
    private javax.swing.JTextField jTextFieldMonitorSoga;
    private javax.swing.JTextField jTextFieldMonitorTirolina;
    private javax.swing.JTextField jTextFieldMonitoresZonaComun;
    private javax.swing.JTextField jTextFieldNinosZonaComun;
    private javax.swing.JTextField jTextFieldPreparacionTirolina;
    private javax.swing.JTextField jTextFieldPuerta1;
    private javax.swing.JTextField jTextFieldPuerta2;
    private javax.swing.JTextField jTextFieldTirandoTirolina;
    private javax.swing.JLabel labelBandejasLimpias;
    private javax.swing.JLabel labelBandejasSuciasMerendero;
    private javax.swing.JLabel labelCampamento;
    private javax.swing.JLabel labelEntradaCampamento;
    private javax.swing.JLabel labelMerendero;
    private javax.swing.JLabel labelMonitorSoga;
    private javax.swing.JLabel labelMonitorTirolina;
    private javax.swing.JLabel labelMonitoresMerendero;
    private javax.swing.JLabel labelMonitoresZonaComun;
    private javax.swing.JLabel labelNinosZonaComún;
    private javax.swing.JLabel labelPuerta1;
    private javax.swing.JLabel labelPuerta2;
    private javax.swing.JLabel labelSoga;
    private javax.swing.JLabel labelTirolina;
    private javax.swing.JLabel labelZonaComun;
    private javax.swing.JPanel panelCampamento;
    private javax.swing.JPanel panelEntradaCampamento;
    private javax.swing.JPanel panelMerendero;
    private javax.swing.JPanel panelPuerta1;
    private javax.swing.JPanel panelPuerta2;
    private javax.swing.JPanel panelSoga;
    private javax.swing.JPanel panelTirolina;
    private javax.swing.JPanel panelZonaComun;
    // End of variables declaration//GEN-END:variables

    public JTextField getjTextFieldAcabandoTirolina() {
        return jTextFieldAcabandoTirolina;
    }

    public JTextField getjTextFieldEquipoASoga() {
        return jTextFieldEquipoASoga;
    }

    public JTextField getjTextFieldEquipoBSoga() {
        return jTextFieldEquipoBSoga;
    }

    public JTextField getjTextFieldListaBandejasLimpiasMerendero(){
        return jTextFieldListaBandejasLimpiasMerendero;
    }
    public JTextField getjTextFieldListaBandejasSuciasMerendero() {
        return jTextFieldListaBandejasSuciasMerendero;
    }

    public JTextField getjTextFieldListaEsperaMerendero() {
        return jTextFieldListaEsperaMerendero;
    }

    public JTextField getjTextFieldListaEsperaSoga() {
        return jTextFieldListaEsperaSoga;
    }

    public JTextField getjTextFieldListaEsperaTirolina() {
        return jTextFieldListaEsperaTirolina;
    }

    public JTextField getjTextFieldListaMonitoresMerendero() {
        return jTextFieldListaMonitoresMerendero;
    }

    public JTextField getjTextFieldListaNinosMerendero() {
        return jTextFieldListaNinosMerendero;
    }

    public JTextField getjTextFieldMonitorSoga() {
        return jTextFieldMonitorSoga;
    }

    public JTextField getjTextFieldMonitorTirolina() {
        return jTextFieldMonitorTirolina;
    }

    public JTextField getjTextFieldMonitoresZonaComun() {
        return jTextFieldMonitoresZonaComun;
    }

    public JTextField getjTextFieldNinosZonaComun() {
        return jTextFieldNinosZonaComun;
    }

    public JTextField getjTextFieldPreparacionTirolina() {
        return jTextFieldPreparacionTirolina;
    }

    public JTextField getjTextFieldPuerta1() {
        return jTextFieldPuerta1;
    }

    public JTextField getjTextFieldPuerta2() {
        return jTextFieldPuerta2;
    }

    public JTextField getjTextFieldTirandoTirolina() {
        return jTextFieldTirandoTirolina;
    }

    public JButton getBotonPararReanudar() {
        return botonPararReanudar;
    }

    public Campamento getCampamentoServidor() {
        return campamentoServidor;
    }

    public Soga getSogaServidor() {
        return sogaServidor;
    }

    public Tirolina getTirolinaServidor() {
        return tirolinaServidor;
    }

    public Merendero getMerenderoServidor() {
        return merenderoServidor;
    }

    public ZonaComun getZonaComunServidor() {
        return zonaComunServidor;
    }

    public Log getArchivoLog() {
        return archivoLog;
    }

    public Paso getPaso() {
        return paso;
    }

    public boolean isBotonPulsado() {
        return botonPulsado;
    }

    public void setBotonPulsado(boolean botonPulsado) {
        this.botonPulsado = botonPulsado;
    }
}