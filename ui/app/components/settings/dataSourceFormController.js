'use strict';

angular.module('sputnik').controller('DataSourceFormController', function ($scope, $uibModalInstance, model, dataProfiles, httpUtils) {

    function init() {
        $scope.model = angular.copy(model);
        $scope.dataFormats = ['JSON', 'PROPERTIES'];
        $scope.dataProfiles = dataProfiles;
    }

    function save() {
        if ($scope.model.id) {
            return httpUtils.put("/dataSources/" + $scope.model.id, $scope.model);
        } else {
            return httpUtils.post("/dataSources/", $scope.model);
        }
    }

    $scope.clickOk = function (form) {
        if (!form || form.$valid) {
            save().then(function () {
                $uibModalInstance.close();
            });
        }
    };

    $scope.clickCancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.clickRemove = function () {
        var yes = confirm("Are you sure you want to remove this data source?");
        if (yes) {
            httpUtils.delete("/dataSources/" + $scope.model.id).then(function () {
                $uibModalInstance.close();
            });
        }
    };

    init();

});
