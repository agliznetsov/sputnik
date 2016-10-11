'use strict';

angular.module('sputnik', [
    'ngStorage', 'ngRoute'
]).config(function ($routeProvider, $httpProvider) {
    $routeProvider
        .when('/home', {
            templateUrl: 'components/home/home.html',
            controller: 'HomeController'
        })
        .when('/settings', {
            templateUrl: 'components/settings/settings.html'
        })
        .otherwise({
            redirectTo: '/home'
        });

});
