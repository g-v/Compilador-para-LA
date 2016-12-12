# Compilador-para-LA

Autores:  
Bruno Greco - RA: 489271 - @realm087  
Bruno Guerra - RA: 489360 - @b-guerra  
Guilherme Vazquez - RA: 489255 - @g-v  
Lucas Macedo - RA: 489409  


## Instruções para Utilização  
UNIX  
`$ java -jar CorretorTrabalho1/CorretorTrabalho1.jar "java -jar dist/T1.jar" gcc temp casosDeTesteT1 "489255, 489271, 4892360, 489409" tudo`  
ou  
`$ sh corretor.sh`  

Windows  
`> java -jar c:\caminho_para\CorretorTrabalho1\CorretorTrabalho1.jar "java jar
c:\caminho_para\dist\T1.jar" gcc c:\caminho_para\temp c:\caminho_para\casosDeTesteT1 "489255, 489271, 4892360, 489409" tudo`  

Ou se quiser construir o projeto localmente:  
## Instruções para Compilação  

1 - Instalar `gradle`  

----------------------------

### >>>> UNIX  
Se o Gradle não estiver disponível no Gerenciador de Pacotes do seu SO, uma maneira de instalar é:  

### SDKMAN  
Baixar SDKMAN:  
`$ curl -s https://get.sdkman.io | bash`  

Abra um novo terminal e instale o Gradle    
`$ sdk install gradle 3.2.1`  

### >>>> WINDOWS
Baixar o instalador em: https://gradle.org/gradle-download/  

----------------------------

2 - Após instalar o gradle, abra o terminal e vá para a pasta raiz do projeto. Execute o comando abaixo para compilar a gramática:  
`$ gradle antlr4`  

3 - Em seguida, entre na pasta `src`:  
`$ cd src`  

4 - Execute:  
UNIX:  
`$ java -jar CorretorTrabalho1/CorretorTrabalho1.jar "java -jar dist/T1.jar" gcc temp casosDeTesteT1 "489255, 489271, 4892360, 489409" tudo`  
ou  
`$ sh corretor.sh`  

Windows  
`> java -jar c:\caminho_para\CorretorTrabalho1\CorretorTrabalho1.jar "java jar
c:\caminho_para\dist\T1.jar" gcc c:\caminho_para\temp c:\caminho_para\casosDeTesteT1 "489255, 489271, 4892360, 489409" tudo` 

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
         -> *.java
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
