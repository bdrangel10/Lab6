var app=angular.module("TCP_Login",[])
app.controller("controlador", function($scope)
{
    
    $scope.titulo="INGRESA Y DISFRUTA";
    $scope.resultado={"error":false, "logueado":false, "msj":""};
    $scope.registro={"username":"", "password":"", "password1":"","accion":""};
    $scope.console="";
       
    $scope.entrar = function()
    {
        //Loguin socket
        $scope.registro.accion="ingresar";   
        var socket = new WebSocket("ws://localhost:8080/TCPLogin/auth",[]);        
        socket.onopen = function ()
        {
            $scope.$apply(function () {
                escribirConsola("WS: Creado en ws://localhost:8080/TCPLogin/auth");
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
                    $scope.titulo = "BIENVENIDO " + $scope.registro.username;
                    $scope.console += "\nWS: RECIBIDO:" + recibido;
                }
                $scope.registro.password = "";
            });

        };
        
    }
    
    $scope.registrar=function()
    {
        $scope.registro.accion="registrar";   
        var socket = new WebSocket("ws://localhost:8080/TCPLogin/auth",[]);
        socket.onopen = function ()
        {
            $scope.$apply(function () 
            {
                $scope.console+="\nWS: Creado en ws://localhost:8080/TCPLogin/auth";
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
                    $scope.titulo = "BIENVENIDO " + $scope.registro.username;
                    $scope.console += "\nWS: RECIBIDO:" + obtenido;
                    var formulario = document.getElementById("divregistro");
                    formulario.style.display="none";

                }
                $scope.registro.password = "";
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
        var formulario = document.getElementById("divregistro");
        formulario.style.display="block";
    }
    
    $scope.limpiarConsola=function()
    {
        $scope.console="";
    }
    
        
    escribirConsola = function (msj)
    {        
            $scope.console += "\n"+msj;
            console.log(msj);
  
    };
    
    encriptarContraseña= function(pass)
    {
        return md5(pass);
    }
    
})



