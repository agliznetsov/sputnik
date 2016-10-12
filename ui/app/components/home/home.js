'use strict';

angular.module('sputnik').controller('HomeController', function ($scope, $routeParams, $q, httpUtils, $timeout) {

    $scope.model = {
        hosts: [],
        ranges: [],
        reports: [],
        range: 'day'
    };

    $scope.formatDate = function (moment) {
        return moment ? moment.format('ll') + ' ' + moment.format('HH:mm') : undefined;
    };

    $scope.isCustomRange = function () {
        return typeof $scope.model.range === 'number';
    };

    $scope.selectRange = function (value) {
        $scope.model.range = value;
        loadData()
    };

    $scope.refresh = function () {
        loadData();
    };

    function init() {
        $scope.$on("report-rendered", reportRendered);
        var requests = {
            dataSources: httpUtils.get("/dataSources"),
            properties: httpUtils.get("/properties")
        };
        $q.all(requests).then(function (result) {
            $scope.model.properties = result.properties.data;
            $scope.model.ranges = [];
            for (var i = 1; i <= $scope.model.properties.archiveDays; i++) {
                $scope.model.ranges.push({value: i, name: i + " Day" + (i > 1 ? "s" : "")});
            }
            setHosts(result.dataSources.data);
            selectSource();
        });
    }

    function setHosts(data) {
        $scope.model.hosts = [];
        var map = _.groupBy(data, 'groupName');
        for (var key in map) {
            var host = {
                name: key,
                sources: map[key]
            };
            $scope.model.hosts.push(host);
        }
    }

    function selectSource() {
        if ($routeParams.src) {
            var names = $routeParams.src.split('/');
            $scope.model.hostName = names[0];
            $scope.model.sourceName = names[1];
            loadData();
        }
    }

    function loadData() {
        var end = window.moment();
        var start = getStart(end);
        var seconds = (end.unix() - start.unix());
        var resolution = $scope.isCustomRange() ? $scope.model.properties.dataRate : Math.floor(seconds / $scope.model.properties.archiveRows);
        var host = _.find($scope.model.hosts, {name: $scope.model.hostName});
        if (host) {
            var source = _.find(host.sources, {name: $scope.model.sourceName});
            if (source) {
                httpUtils.get("/data/" + host.name + "/" + source.name + "?start=" + start.unix() + "&end=" + end.unix() + "&resolution=" + resolution).then(function (response) {
                    displayData(response.data);
                });
            }
        }
    }

    function getStart(end) {
        var start = end.clone();
        if ($scope.isCustomRange()) {
            start.subtract($scope.model.range, 'days');
        } else {
            start.subtract(1, $scope.model.range + 's');
        }
        return start;
    }

    function displayData(data) {
        data.lastUpdate = toMoment(data.lastUpdate);
        for (var i = 0; i < data.timestamps.length; i++) {
            data.timestamps[i] *= 1000;
        }
        console.info("data points #", data.timestamps.length);
        $scope.model.data = data;
        $scope.model.reports = [];
        $scope.model.renderCount = data.dataProfile.graphs.length;
        $timeout(function () {
            _.forEach(data.dataProfile.graphs, function (report) {
                report.timestamps = data.timestamps;
                report.values = data.values;
                $scope.model.reports.push(report);
            });
        });
    }

    function reportRendered() {
        $scope.model.renderCount--;
        console.info("report-rendered", $scope.model.renderCount);
    }

    function toMoment(time) {
        var moment = window.moment(time * 1000);
        moment.utcOffset(new Date().getTimezoneOffset() * -1);
        return moment;
    }

    init();

});
