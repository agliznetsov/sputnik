'use strict';

angular.module('sputnik').directive('profileEditor', function () {
    return {
        restrict: 'E',
        templateUrl: 'components/profiles/profile.html',
        replace: true,
        scope: {
            model: '='
        },
        controller: function ($scope, httpUtils, notificationService) {

            $scope.cancel = function () {
                $scope.$emit("cancel");
            };

            $scope.save = function () {
                $scope.errorMessage = undefined;
                httpUtils.post("/dataProfiles/", $scope.model).then(function () {
                    $scope.$emit("save");
                }, function (err) {
                    notificationService.error(err);
                });
            };

            $scope.select = function (type, object, parent) {
                $scope.selectionType = type;
                $scope.selection = object;
                $scope.parent = parent;
            };

            $scope.addGraph = function () {
                var graph = {
                    name: 'graph',
                    dataSeries: []
                };
                $scope.selection.graphs.push(graph);
                $scope.select('graph', graph);
            };

            $scope.addSerie = function () {
                var serie = {
                    name: 'serie',
                    serieType: $scope.serieTypes[0]
                };
                $scope.selection.dataSeries.push(serie);
                $scope.select('serie', serie, $scope.selection);
            };

            $scope.removeGraph = function () {
                var i = _.indexOf($scope.model.graphs, $scope.selection);
                $scope.model.graphs.splice(i, 1);
                $scope.select();
            };

            $scope.removeSerie = function () {
                var i = _.indexOf($scope.parent.dataSeries, $scope.selection);
                $scope.parent.dataSeries.splice(i, 1);
                $scope.select();
            };

        },
        link: function (scope, element, attr) {
            scope.select('profile', scope.model);
            scope.serieTypes = ['GAUGE', 'DERIVE'];
            scope.serieFunctions = ['SUM', 'AVG'];
        }
    };
});

