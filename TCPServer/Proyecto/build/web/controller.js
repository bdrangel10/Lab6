var app=angular.module("TCP_Login",[])
app.controller("controlador", function($scope)
{
    var rutaServer="ws:/localhost:8080/TCPServer/auth";
    $scope.titulo="INGRESA Y DISFRUTA";
    $scope.resultado={"error":false, "logueado":false, "msj":""};
    $scope.registro={"username":"", "password":"", "password1":"","accion":""};
    $scope.console = "";
    $scope.suscripciones = [];
    $scope.canales = [];
    $scope.vercanal = "";
    $scope.verusuario = "";
    
    $scope.vistaSuscritos=false;
    $scope.vistaAdministrar=false;
    $scope.vistaTodos=false;

    //SI ESTÁ LOGUEADO RETORNA TANTO SUSCRIPCIONES COMO CANALES

    $scope.entrar = function()
    {
        //Loguin socket
        $scope.registro.accion="ingresar";   
        var socket = new WebSocket(rutaServer);        
        socket.onopen = function ()
        {
            $scope.$apply(function () {
                escribirConsola("WS: Creado en "+rutaServer);
                //$scope.registro.password=md5.createHash($scope.registro.password||"");
                socket.send(JSON.stringify($scope.registro));
                escribirConsola("WS: ENVIADO: " + JSON.stringify($scope.registro));
            });
        };
        socket.onmessage = function(event)
        {
            var obtenido = event.data;
            $scope.$apply(function () {
                escribirConsola("WS: RECIBIDO: " + obtenido);
                $scope.resultado = JSON.parse(obtenido);
                if ($scope.resultado.logueado)
                {
                    $scope.vistaSuscritos=true;
                    $scope.titulo = "BIENVENIDO " + $scope.registro.username;
                    $scope.console += "\nWS: RECIBIDO:" + obtenido;
                    $scope.suscripciones=$scope.resultado.suscripciones;
                    $scope.canales=$scope.resultado.canales;
                    var formulario = document.getElementById("divregistro");
                    formulario.style.display="none";
                }
                $scope.registro.password = "";
                $scope.registro.password1 = "";
            });

        };
        
    }
    
    $scope.registrar=function()
    {
        $scope.registro.accion="registrar";   
        var socket = new WebSocket(rutaServer);
        socket.onopen = function ()
        {
            $scope.$apply(function () 
            {
                $scope.console+="\nWS: Creado en "+rutaServer;
                //$scope.registro.password=md5($scope.registro.password);
                socket.send(JSON.stringify($scope.registro))
                $scope.console+="\nWS: ENVIADO: "+JSON.stringify($scope.registro);
            });
            
        };
        socket.onmessage = function(event)
        {
            var obtenido = event.data;
            console.log("WS: RECIBIDO: " + obtenido);
            $scope.$apply(function () {
                $scope.resultado = JSON.parse(obtenido);
                if ($scope.resultado.logueado)
                {
                    $scope.vistaSuscritos=true;
                    $scope.titulo = "BIENVENIDO " + $scope.registro.username;
                    $scope.console += "\nWS: RECIBIDO:" + obtenido;
                    var formulario = document.getElementById("divregistro");
                    formulario.style.display="none";

                }
                $scope.registro.password = "";
                $scope.registro.password1 = "";
            });

        };
       
    }
    
    $scope.salir=function()
    {
        //Registro socket
        $scope.resultado.logueado=false;
        $scope.registro.password="";
        $scope.registro.password1="";
        $scope.titulo="INGRESA Y DISFRUTA";
        $scope.suscripciones= [];
        $scope.canales = [];
        $scope.vercanal="";
        $scope.verusuario="";
        var formulario = document.getElementById("divregistro");
        formulario.style.display="block";
    }
    
    $scope.limpiarConsola=function()
    {
        $scope.console="";
    }
    
    $scope.ver= function(usuario,canal)
    {
        $scope.vercanal=canal;
        $scope.verusuario=usuario;
        alert("El usuario desea ver el canal del usuario "+usuario+" que está en el puerto "+canal);
    }
    
        
    escribirConsola = function (msj)
    {      
            console.log(msj);            
            var consola =document.getElementById("consola");
            consola.value+="\n"+msj;
            var msj=consola.value;
            //consola.focus();            
            consola.focus();
            consola.value="";
            consola.value=msj;
 
            // Movemos el scroll
            consola.scrollHeight=msj.length;
  
    };
    
    encriptarContraseña= function(pass)
    {
        return md5(pass);
    }
    
    $scope.administrar=function()
    {
        $scope.vistaAdministrar=true;  
        $scope.vistaSuscritos=false;
        $scope.vistaTodos=false;
    };
    
    $scope.guardar=function()
    {
        $scope.vistaAdministrar=false;
        $scope.vistaSuscritos=true;
        $scope.vistaTodos=false; 
        
    };
    
    $scope.listarTodos=function()
    {
        $scope.vistaAdministrar=false;
        $scope.vistaSuscritos=false;
        $scope.vistaTodos=true; 
    };
    
    $scope.listarSuscripciones=function()
    {
        $scope.vistaAdministrar=false;
        $scope.vistaSuscritos=true;
        $scope.vistaTodos=false; 
    };
    
})



