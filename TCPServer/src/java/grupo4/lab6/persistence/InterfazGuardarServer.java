/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grupo4.lab6.persistence;

import grupo4.lab6.websocket.AdministradorUsuarios;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author administrador
 */
public class InterfazGuardarServer extends JFrame implements ActionListener
{
    private JButton btnGuardar;
    
    public InterfazGuardarServer()
    {
        btnGuardar=new JButton("Guardar estado actual");
        btnGuardar.addActionListener(this);
        this.add(btnGuardar);
        this.setVisible(true);
    }

    
    public void actionPerformed(ActionEvent e) 
    {
        AdministradorUsuarios.darInstancia().guardarEstado();
    }
    
    public static void main(String[] args) throws Exception
    {
        new InterfazGuardarServer();         
    }
    
}
