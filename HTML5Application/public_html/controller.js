var app=angular.module("TCP_Login",[])
app.controller("controlador", function($scope)
{
    $scope.titulo="INGRESA Y DISFRUTA";
    $scope.error=false; 
    $scope.logueado=false;
       
    $scope.entrar = function()
    {
        //Loguin socket
        $scope.logueado=true;
        $scope.titulo="BIENVENIDO "+$scope.username;
    }
    
    $scope.registrar=function()
    {
        //Registro socket
        $scope.logueado=true;
        $scope.titulo="BIENVENIDO "+$scope.username;
    }
    
    $scope.salir=function()
    {
        //Registro socket
        $scope.logueado=false;
        $scope.password="";
        $scope.password1="";
        $scope.titulo="INGRESA Y DISFRUTA";
    }
    
})



