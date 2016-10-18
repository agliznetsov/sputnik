'use strict';

angular.module('sputnik', [
    'ngRoute', 'ui.bootstrap'
]).config(function ($routeProvider, $httpProvider) {
    $routeProvider
        .when('/home', {
            templateUrl: 'components/home/home.html',
            controller: 'HomeController'
        })
        .when('/settings', {
            templateUrl: 'components/settings/settings.html',
            controller: 'SettingsController'
        })
        .otherwise({
            redirectTo: '/home'
        });

});
