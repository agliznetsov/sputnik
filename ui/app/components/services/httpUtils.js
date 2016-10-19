'use strict';

angular.module('sputnik').factory('httpUtils', function ($http, $rootScope) {

    var timeout = 5000;

    function headers() {
        var headers = {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        };
        if ($rootScope.token) {
            headers.Authorization = 'Bearer ' + $rootScope.token;
        }
        return headers;
    }

    return {

        get: function (link) {
            return $http({
                method: 'GET',
                url: link,
                headers: headers(),
                cache: false,
                timeout: timeout
            });
        },

        post: function (link, body) {
            return $http({
                method: 'POST',
                url: link,
                data: body,
                headers: headers(),
                cache: false,
                timeout: timeout
            });
        },

        delete: function (link) {
            return $http({
                method: 'DELETE',
                url: link,
                headers: headers(),
                cache: false,
                timeout: timeout
            });
        },

        put: function (link, body) {
            return $http({
                method: 'PUT',
                url: link,
                data: body,
                headers: headers(),
                cache: false,
                timeout: timeout
            });
        }

    };
});


