'use strict';

angular.module('sputnik').controller('SigninController', function ($scope, $rootScope, $location, httpUtils) {

    $scope.clickOk = function (form) {
        if (!form || form.$valid) {
            signin();
        }
    };

    function error(response) {
        if (response.data && response.data.message)
            $scope.errorMessage = response.data.message;
        else
            $scope.errorMessage = 'Request failed!';
    }

    function signin() {
        var username = $scope.username || 'Anonymous';
        return httpUtils.post("/signin", {
            username: username,
            password: $scope.password
        }).then(function (response) {
            $rootScope.user = username;
            $rootScope.token = response.data.token;
            $location.path("#/home");
        }, function (err) {
            error(err);
        })
    }

});
