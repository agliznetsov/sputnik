'use strict';

angular.module('sputnik').controller('HomeController', function ($scope, $routeParams, $q, $timeout, httpUtils) {

    $scope.model = {
        hosts: [],
        ranges: [],
        reports: []
    };

    $scope.formatDate = function (moment) {
        return moment ? moment.format('ll') + ' ' + moment.format('HH:mm') : undefined;
    };

    $scope.isCustomRange = function () {
        return typeof $scope.model.range === 'number';
    };

    $scope.selectRange = function (value) {
        setRange(value);
        saveConfig();
        loadData()
    };

    $scope.refresh = function () {
        loadData();
    };

    $scope.setColumns = function (value) {
        setColumns(value);
        saveConfig();
        displayData();
    };

    $scope.chartsListToggled = function (open) {
        if (!open) {
            saveConfig();
            displayData();
        }
    };

    $scope.chartsLabel = function () {
        if ($scope.model.charts) {
            var count = _.filter($scope.model.charts, 'enabled').length;
            return count == $scope.model.charts.length ? 'all' : count;
        } else {
            return '';
        }
    };

    function init() {
        $scope.$on("report-rendered", reportRendered);
        selectSource();
        loadConfig();
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
            loadData();
        });
    }

    function loadConfig() {
        if ($scope.model.src) {
            var config = get($scope.model.src);
            if (config) {
                setColumns(config.columns || 2);
                setRange(config.range || 'day');
                if (config.charts) {
                    $scope.model.charts = [];
                    for(var key in config.charts) {
                        $scope.model.charts.push({
                            name: key,
                            enabled: config.charts[key]
                        });
                    }
                }
            } else {
                setColumns(2);
                setRange('day');
            }
        }
    }

    function saveConfig() {
        if ($scope.model.src) {
            var config = {
                columns: $scope.model.columns,
                range: $scope.model.range,
                charts: {}
            };
            $scope.model.charts.forEach(function (it) {
                config.charts[it.name] = it.enabled;
            });
            set($scope.model.src, config);
        }
    }

    function get(key) {
        var data = window.localStorage.getItem(key);
        return data ? angular.fromJson(data) : undefined;
    }

    function set(key, value) {
        var data = angular.toJson(value);
        window.localStorage.setItem(key, data);
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

    function setColumns(value) {
        $scope.model.columns = value;
        $scope.model.reportClass = "col-" + value;
    }

    function setRange(value) {
        $scope.model.range = value;
    }

    function selectSource() {
        if ($routeParams.src) {
            $scope.model.src = $routeParams.src;
            var names = $routeParams.src.split('/');
            $scope.model.hostName = names[0];
            $scope.model.sourceName = names[1];
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
                    setCharts(response.data);
                    response.data.lastUpdate = toMoment(response.data.lastUpdate);
                    for (var i = 0; i < response.data.timestamps.length; i++) {
                        response.data.timestamps[i] *= 1000;
                    }
                    console.info("data points #", response.data.timestamps.length);
                    $scope.model.data = response.data;
                    displayData();
                });
            }
        }
    }

    function setCharts(data) {
        var map;
        if ($scope.model.charts) {
            map = _.keyBy($scope.model.charts, 'name');
        }
        $scope.model.charts = data.dataProfile.graphs.map(function (it) {
            return {
                name: it.name,
                description: it.description,
                enabled: (map && map[it.name]) ? map[it.name].enabled : true
            }
        })
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

    function displayData() {
        if ($scope.model.data) {
            var map = _.keyBy($scope.model.charts, 'name');
            $scope.model.reports = [];
            $scope.model.renderCount = _.filter($scope.model.charts, 'enabled').length;
            $timeout(function () {
                _.forEach($scope.model.data.dataProfile.graphs, function (report) {
                    if (map[report.name].enabled) {
                        report.timestamps = $scope.model.data.timestamps;
                        report.values = $scope.model.data.values;
                        $scope.model.reports.push(report);
                    }
                });
            });
        }
    }

    function reportRendered() {
        $scope.model.renderCount--;
    }

    function toMoment(time) {
        var moment = window.moment(time * 1000);
        moment.utcOffset(new Date().getTimezoneOffset() * -1);
        return moment;
    }

    init();

});
