'use strict';

angular.module('sputnik').factory('chartUtils', function ($timeout) {

    Chart.defaults.global.responsive = true;
    Chart.defaults.global.animation = 0;
    Chart.defaults.global.elements.line.borderWidth = 1;
    Chart.defaults.global.elements.line.stepped = true;
    Chart.defaults.global.elements.point.radius = 0;
    Chart.defaults.global.tooltips.enabled = true;
    Chart.defaults.global.tooltips.mode = 'x-axis';

    var units = [
        {days: 2, unit: 'hour', format: 'HH:mm'},
        {days: 14, unit: 'day', format: 'ddd'},
        {days: 60, unit: 'day', format: 'DD'},
        {days: 9999999999, unit: 'month', format: 'MMM'}
    ];
    var DAY = 1000 * 60 * 60 * 24;

    function draw(model, element) {
        var ts = model.timestamps;
        var days = (ts[ts.length - 1] - ts[0]) / DAY;
        var unit = _.find(units, function (it) {
            return days < it.days;
        });
        var min, max;
        var datasets = [];
        _.forEach(model.dataSeries, function (it) {
            if (it.min != undefined) {
                min = it.min;
            }
            if (it.max != undefined) {
                max = it.max;
            }
            var data = model.values[it.name];
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    if (it.min != undefined) {
                        if (data[i] < it.min) {
                            data[i] = undefined;
                        }
                    }
                    if (it.max != undefined) {
                        if (data[i] > it.max) {
                            data[i] = undefined;
                        }
                    }
                }
                datasets.push({
                    label: it.name,
                    data: data,
                    fill: it.fill,
                    borderColor: it.color,
                    backgroundColor: it.color
                });
            }
        });
        _.reverse(datasets);

        new Chart(element, {
            type: 'line',
            data: {
                labels: ts,
                datasets: datasets
            },
            options: {
                title: {
                    display: true,
                    text: model.description
                },
                legend: {
                    position: 'bottom'
                },
                tooltips: {
                    custom: function (tooltip) {
                        if (tooltip) {
                            tooltip.y = 0;
                        }
                    },
                    callbacks: {
                        label: function (item, data) {
                            return data.datasets[item.datasetIndex].label + ": " + human(item.yLabel);
                        }
                    }
                },
                scales: {
                    xAxes: [{
                        type: "time",
                        ticks: {
                            maxRotation: 0,
                            autoSkip: true
                        },
                        time: {
                            unit: unit.unit,
                            tooltipFormat: 'lll HH:mm',
                            displayFormats: {
                                hour: unit.format,
                                day: unit.format,
                                month: unit.format
                            }
                        }
                    }],
                    yAxes: [{
                        type: "linear",
                        ticks: {
                            min: min,
                            max: max,
                            callback: function (value, index, values) {
                                return human(value);
                            }
                        }
                    }]
                }
            }
        });
    }

    function human(arg) {
        var symbols = ["K", "M", "G", "T", "P", "E", "Z", "Y"];
        var num = Number(arg);
        var neg = num < 0;
        if (neg) {
            num = -num;
        }
        var e = Math.floor(Math.log(num) / Math.log(1000));
        if (e >= 1 && e <= 8) {
            var val = num / Math.pow(1000, e);
            val = Math.floor(val * 100) / 100;
            if (neg) {
                val = -val;
            }
            return val + ' ' + symbols[e - 1];
        } else {
            val = Math.floor(arg * 100) / 100;
            return String(val);
        }
    }

    return {
        draw: draw
    }

});
