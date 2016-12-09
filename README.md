# Compilador-para-LA

Autores:  
Bruno Greco - RA: 489271 - @realm087  
Bruno Guerra - RA: 489360 - @b-guerra  
Guilherme Vazquez - RA: 489255 - @g-v  
Lucas Macedo - RA: 489409  

## Como Utilizar

TODO  

Linux/Mac  
```
$ cd dist  
$ 
```
## Estrutura do Diretório
```
/  
   -> dist/  
      -> T1.jar  
  
   -> src/  
      -> main/antlr/  
         -> LA.g4  
        
      -> trabalho1/  
         -> AnalisadorSemantico.java  
         -> GeradorCodigo.java  

   -> examples/  
      -> 1.declaracao_leitura_impressao_inteiro.alg  
      -> 2.declaracao_leitura_impressao_real.alg  
      -> saida.txt  
      -> saidaGeradorDeCodigo.txt  
```   




Compilador para a linguagem LA, que usa ANTLR 4.  

Análise Léxica + Análise Sintática  

Análise Semântica  

Geração de Código (em C)  

----------------------------

Erros léxicos e sintáticos:  

Verificar a gramática em src/main/antlr/LA.g4  

----------------------------

Erros semânticos a serem relatados:  

1) Identificador (variável, constante, procedimento, função, tipo) já declarado anteriormente no escopo  
2) Tipo não declarado  
3) Identificador (variável, constante, procedimento, função) não declarado  
4) Incompatibilidade entre argumentos e parâmetros formais (número, ordem e tipo) na chamada de um procedimento ou uma função  
5) Atribuição não compatível com o tipo declarado  
6) Uso do comando 'retorne' em um escopo não permitido  

-----------------------------
