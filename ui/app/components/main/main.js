'use strict';

angular.module('sputnik').controller('MainController', function ($scope, $rootScope, $location, notificationService) {

    $scope.menu = {
        left: [],
        right: [
            {
                name: 'Settings',
                url: 'settings',
                icon: 'fa-cog'
            }
        ]
    };

    $scope.getUrl = function (item) {
        return '#/' + item.url;
    };

    $scope.isVisible = function (item) {
        return $rootScope.user;
    };

    $scope.isActive = function (path) {
        return $location.path().indexOf(path) === 0;
    };

    $scope.removeNotification = function (key) {
        notificationService.removeNotification(key);
    };

    $scope.signout = function() {
        delete $rootScope.user;
        delete $rootScope.token;
        $location.path("#/home");
    }


});
