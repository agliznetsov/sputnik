// Generated on 2015-09-01 using generator-angular 0.12.1
'use strict';

module.exports = function (grunt) {

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    // Automatically load required Grunt tasks
    require('jit-grunt')(grunt, {
        useminPrepare: 'grunt-usemin',
        ngtemplates: 'grunt-angular-templates'
    });

    // Define the configuration for all the tasks
    grunt.initConfig({

        // Empties folders to start fresh
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        'dist/{,*/}*',
                        '!dist/.git{,*/}*'
                    ]
                }]
            }
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: 'app/index.html',
            options: {
                dest: 'dist',
                flow: {
                    html: {
                        steps: {
                            js: ['concat'],
                            css: ['concat']
                        },
                        post: {}
                    }
                }
            }
        },

        // Performs rewrites based on filerev and the useminPrepare configuration
        usemin: {
            html: ['dist/{,*/}*.html'],
            css: ['dist/styles/{,*/}*.css'],
            js: ['dist/scripts/{,*/}*.js'],
            options: {
                assetsDirs: [
                    'dist',
                    'dist/images',
                    'dist/styles'
                ],
                patterns: {
                    js: [[/(images\/[^''""]*\.(png|jpg|jpeg|gif|webp|svg))/g, 'Replacing references to images']]
                }
            }
        },

        ngtemplates: {
            dist: {
                cwd: 'app',
                src: 'components/**/*.html',
                dest: '.tmp/templateCache.js',
                options: {
                    module: 'sputnik'
                    //Does not work with usemin without uglify step, we have to manually reference the cache in the index.html
                    //concat: 'generated',
                    //usemin: 'scripts/app.js'
                }
            }
        },

        // Copies remaining files to places other tasks can use
        copy: {
            dist: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: 'app',
                        dest: 'dist',
                        src: [
                            'index.html',
                            'images/{,*/}*.*'
                        ]
                    },
                    {
                        expand: true,
                        cwd: '.tmp/images',
                        dest: 'dist/images',
                        src: ['generated/*']
                    },
                    {
                        expand: true,
                        cwd: 'app/bower_components/bootstrap/dist',
                        src: 'fonts/*',
                        dest: 'dist'
                    }
                ]
            }
        }

    });

    grunt.registerTask('build', [
        'clean',
        'useminPrepare',
        'ngtemplates',
        'concat',
        'copy',
        'usemin'
    ]);

    grunt.registerTask('default', [
        'build'
    ]);

};
