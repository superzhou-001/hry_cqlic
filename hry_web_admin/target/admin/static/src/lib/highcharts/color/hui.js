
define(function(require,exports,module){
var highcharts = require("lib/highcharts/highcharts");



        /**
         * (c) 2010-2016 Torstein Honsi
         *
         * License: www.highcharts.com/license
         * 
         * Sand-Signika theme for Highcharts JS
         * @author Torstein Honsi
         */

        'use strict';
        /* global document */
        // Load the fonts
        Highcharts.createElement('link', {
            href: './static/lib/highcharts/css/hui.css',
            rel: 'stylesheet',
            type: 'text/css'
        }, null, document.getElementsByTagName('head')[0]);

        // Add the background image to the container
        Highcharts.wrap(Highcharts.Chart.prototype, 'getContainer', function(proceed) {
            proceed.call(this);
            this.container.style.background = 'url(./static/lib/highcharts/img/sand.png)';
        });


        Highcharts.theme = {
            colors: ['#f45b5b', '#8085e9', '#8d4654', '#7798BF', '#aaeeee', '#ff0066', '#eeaaee',
                '#55BF3B', '#DF5353', '#7798BF', '#aaeeee'
            ],
            chart: {
                backgroundColor: null,
                style: {
                    fontFamily: 'Signika, serif'
                }
            },
            title: {
                style: {
                    color: 'black',
                    fontSize: '16px',
                    fontWeight: 'bold'
                }
            },
            subtitle: {
                style: {
                    color: 'black'
                }
            },
            tooltip: {
                borderWidth: 0
            },
            legend: {
                itemStyle: {
                    fontWeight: 'bold',
                    fontSize: '13px'
                }
            },
            xAxis: {
                labels: {
                    style: {
                        color: '#6e6e70'
                    }
                }
            },
            yAxis: {
                labels: {
                    style: {
                        color: '#6e6e70'
                    }
                }
            },
            plotOptions: {
                series: {
                    shadow: true
                },
                candlestick: {
                    lineColor: '#404048'
                },
                map: {
                    shadow: false
                }
            },

            // Highstock specific
            navigator: {
                xAxis: {
                    gridLineColor: '#D0D0D8'
                }
            },
            rangeSelector: {
                buttonTheme: {
                    fill: 'white',
                    stroke: '#C0C0C8',
                    'stroke-width': 1,
                    states: {
                        select: {
                            fill: '#D0D0D8'
                        }
                    }
                }
            },
            scrollbar: {
                trackBorderColor: '#C0C0C8'
            },

            // General
            background2: '#E0E0E8'

        };

        // Apply the theme
        Highcharts.setOptions(Highcharts.theme);
        
        module.exports = { 
        		init : function(){
        		}
        }

}); 