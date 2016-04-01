var app=angular.module("TCP_Login",[])
app.controller("controlador", function($scope)
{
    var rutaServer="ws:/localhost:8080/TCPServer/auth";
    $scope.titulo="INGRESA Y DISFRUTA";
    $scope.resultado={"error":false, "logueado":false, "msj":""};
    $scope.registro={"username":"", "password":"", "password1":"","accion":""};
    $scope.console2 = "";
    $scope.console3 = "";
    $scope.console1 = "";
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
                    $scope.vistaTodos=false;
                    $scope.vistaAdministrar=false;
                    $scope.titulo = "BIENVENIDO " + $scope.registro.username;
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
                escribirConsola("\nWS: Creado en "+rutaServer);
                //$scope.registro.password=md5($scope.registro.password);
                socket.send(JSON.stringify($scope.registro))
                escribirConsola("\nWS: ENVIADO: "+JSON.stringify($scope.registro));
            });
            
        };
        socket.onmessage = function(event)
        {
            var obtenido = event.data;
            $scope.$apply(function () {
                escribirConsola("WS: RECIBIDO: " + obtenido);
                $scope.resultado = JSON.parse(obtenido);
                $scope.resultado.msj1=$scope.resultado.msj;
                $scope.resultado.msj="";
                if ($scope.resultado.logueado)
                {
                    $scope.vistaSuscritos=true;
                    $scope.vistaTodos=false;
                    $scope.vistaAdministrar=false;
                    $scope.titulo = "BIENVENIDO " + $scope.registro.username;
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
    
    $scope.salir=function()
    {
        //Registro socket
        $scope.resultado.logueado=false;
        $scope.resultado.error=false;
        $scope.resultado.msj="";
        $scope.resultado.canales=[];
        
        $scope.registro.password="";
        $scope.registro.password1="";
        $scope.registro.username="";
        $scope.registro.accion="";
                
        $scope.titulo="INGRESA Y DISFRUTA";
        $scope.suscripciones= [];
        $scope.canales = [];
        $scope.vercanal = "";
        $scope.verusuario = "";
        
        var formulario = document.getElementById("divregistro");
        formulario.style.display = "inline";
        document.getElementById("div_login").style.display="inline-block";

        $scope.vistaSuscritos = false;
        $scope.vistaAdministrar = false;
        $scope.vistaTodos = false;
        
        
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
            $scope.console1=$scope.console2;
            $scope.console2=$scope.console3;
            $scope.console3=msj;
  
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
        
        $scope.registro.accion="guardar";
        $scope.registro.password="";
        var socket = new WebSocket(rutaServer);
        socket.onopen = function ()
        {
            $scope.$apply(function () 
            {
                $scope.registro.suscripciones=$scope.suscripciones;
                escribirConsola("\nWS: Creado en "+rutaServer);
                socket.send(JSON.stringify($scope.registro));
                escribirConsola("\nWS: ENVIADO: "+JSON.stringify($scope.registro));
            });
            
        };
        socket.onmessage = function(event)
        {
            var obtenido = event.data;
            $scope.$apply(function () {
                escribirConsola("WS: RECIBIDO: " + obtenido);
                $scope.resultado = JSON.parse(obtenido);
                $scope.resultado.msj1=$scope.resultado.msj;
                $scope.resultado.msj="";
                if ($scope.resultado.logueado)
                {
                    $scope.vistaSuscritos=true;
                    $scope.vistaTodos=false;
                    $scope.vistaAdministrar=false;
                    $scope.titulo = "BIENVENIDO " + $scope.registro.username;
                    $scope.suscripciones=$scope.resultado.suscripciones;
                    $scope.canales=$scope.resultado.canales;
                }
                $scope.registro.password = "";
                $scope.registro.password1 = "";
            });

        };
        
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
    
    $scope.agregarFavorito=function(nombreCanal)
    {
        var agregar;
        for (var i = 0; i < $scope.canales.length; i++)
        {
            actual = $scope.canales[i];
            if (actual.usuario == nombreCanal)
            {
                agregar = actual;
                break;
            }
        }
        $scope.suscripciones.splice(-1, 0, agregar);

    }
    
    $scope.eliminarFavorito= function (nombreCanal)
    {
        
            for (var i = 0; i < $scope.suscripciones.length; i++)
            {
                if ($scope.suscripciones[i].usuario == nombreCanal)
                {
                    $scope.suscripciones.splice(i, 1);
                    return;
                }
            }
        
    }
    
    $scope.isFavorito=function(nombreCanal)
    {
        for(var i=0;i< $scope.suscripciones.length; i++)
        {
            actual=$scope.suscripciones[i];
            if(actual.usuario==nombreCanal)
            {
                return true;
            }
        }
        return false;        
    }
    
})



