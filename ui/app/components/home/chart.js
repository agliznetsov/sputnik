window.onload = function () {
    Chart.defaults.global.responsive = true;
    Chart.defaults.global.animation = 0;
    // Chart.defaults.global.elements.line.tension = 0;
    Chart.defaults.global.elements.line.stepped = true;
    Chart.defaults.global.elements.point.radius = 0;
    Chart.defaults.global.tooltips.enabled = true;
    Chart.defaults.global.tooltips.mode = 'x-axis';

    draw('day', 120, 'hour', 'HH:mm');
    draw('week', (60 * 60 * 24 * 7 / 1000), 'day', 'ddd');
    draw('month', (60 * 60 * 24 * 31 / 1000), 'day', 'DD');
    draw('year', (60 * 60 * 24 * 365 / 1000), 'month', 'MMM');

};

function draw(name, step, unit, format) {

    var data1 = [];
    var data2 = [];
    var labels = [];
    var value = 0;
    for (var i = 0; i < 1000; i++) {
        value += 10000;
        if (value > 1000000) {
            value = 0;
        }
        if (i > 300 && i < 500) {
            data1.push(Number.NaN);
        } else {
            data1.push(value);
        }
        data2.push(value / 2);
        labels.push(i * 1000 * step);
    }

    var ctx = document.getElementById(name);
    var myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'heap.used',
                    data: data2,
                    fill: true,
                    borderColor: 'blue',
                    backgroundColor: 'blue'
                },
                {
                    label: 'heap',
                    data: data1,
                    fill: true,
                    borderColor: 'orange',
                    backgroundColor: 'orange'
                }
            ]
        },
        options: {
            title: {
                display: true,
                text: name + ", last update " + new Date()
            },
            legend: {
                position: 'bottom'
            },
            tooltips: {
                custom: function(tooltip) {
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
                        autoSkip: true,
                    },
                    time: {
                        unit: unit,
                        tooltipFormat: 'DD.MM.YY HH:mm',
                        displayFormats: {
                            hour: format,
                            day: format,
                            month: format
                        }
                    }
                }],
                yAxes: [{
                    ticks: {
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
    num = Number(arg);
    var neg = num < 0;
    if (neg) {
        num = -num;
    }
    e = Math.floor(Math.log(num) / Math.log(1000));
    if (e >= 1 && e <= 8) {
        val = num / Math.pow(1000, e);
        val = Math.floor(val * 100) / 100;
        if (neg) {
            val = -val;
        }
        return val + ' ' + symbols[e - 1];
    } else {
        return String(arg);
    }
}



