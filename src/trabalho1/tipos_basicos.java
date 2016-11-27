/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

/**
 *
 * @author Esquilo
 */
enum tipos_basicos {
    TIPO_INT(0);
    int valor;
    
    tipos_basicos(int a)
    {
        valor = a;
    }
    
    int Valor()
    {
        return valor;
    }
}

