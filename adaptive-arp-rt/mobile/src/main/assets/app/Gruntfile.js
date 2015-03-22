module.exports = function (grunt) {

    /**
     * Individual configurations for all the tasks
     */
    grunt.initConfig({

        dist_path: '../adaptive-arp-darwin/adaptive-arp-rt/App.Source/www',

        // Watcher: execute $grunt watch
        watch: {
            files: ['src/js', 'src/css', 'src/'], // directories to watch
            tasks: ['test'] // Task to execute on file change
        },

        // HTTP server. Starts a server in localhost pointing to www folder
        'http-server': {
            'src': {
                root: 'src/', // Root folder of the server
                port: '8282',
                host: '127.0.0.1',
                cache: 1,
                showDir: true,
                autoIndex: true,
                ext: "html",
                runInBackground: false
            },
            'www': {
                root: '<%= dist_path %>', // Root folder of the server
                port: '8283',
                host: '127.0.0.1',
                cache: 1,
                showDir: true,
                autoIndex: true,
                ext: "html",
                runInBackground: false
            }
        },

        // Clean folders. Add some sub-tasks to clean every output folder
        clean: {
            'www': {
                src: ['<%= dist_path %>/'],
                options : {
                    force : true
                }
            },
            'www-css': {
                src: ['<%= dist_path %>/css'],
                options : {
                    force : true
                }
            },
            'www-js': {
                src: ['<%= dist_path %>/js'],
                options : {
                    force : true
                }
            },
        },

        // Javascript validator. Skip files in '.jshintignore'
        jshint: {
            options: {
                jshintrc: '.jshintrc', // Jshint configuration options
                ignores: ['src/js/adaptive/**/*.js',
                    'src/js/jquery/**/*.js',
                    'src/js/jquery.mobile/**/*.js',
                    'src/js/jquery.mobile.iscrollview/**/*.js'] // Ignore some js file to validate
            },
            target: {
                src: ['src/js/**/*.js']
            }
        },

        // CSS validator. Skip files with ! in front of in src
        csslint: {
            options: {
                csslintrc: '.csslintrc'
            },
            target: {
                src: ['src/css/**/*.css',
                    '!src/css/jquery.mobile.iscrollview/**/*.css', // Ignore from validation
                    '!src/css/jquery.mobile/**/*.css']
            }
        },

        // Concat javascript files into one single file. Take care of the concat order
        concat: {
            js: {
                src: ['src/js/jquery/jquery-2.1.1.min.js',
                    'src/js/jquery.mobile/jquery.mobile-1.4.5.min.js',
                    'src/js/jquery.mobile.iscrollview/iscroll.js',
                    'src/js/jquery.mobile.iscrollview/jquery.mobile.iscrollview.js',
                    'src/js/adaptive/Adaptive.js',
                    'src/js/**/*.js'],
                dest: '<%= dist_path %>/js/app.js'
            },
            css: {
                src: ['src/css/**/*.css'],
                dest: '<%= dist_path %>/css/style.css'
            }
        },

        // Minify all Javascript, mangle and compress, etc...
        uglify: {
            options: {
                mangle: true,
                compress: true
            },
            target: {
                src: '<%= dist_path %>/js/app.js',
                dest: '<%= dist_path %>/app.min.js'
            }
        },

        // Minimize the css valid file
        cssmin: {
            target: {
                src: '<%= dist_path %>/css/style.css',
                dest: '<%= dist_path %>/style.min.css'
            }
        },

        // Copy files
        copy: {
            'www-images': {
                expand: true,
                cwd: 'src/css/jquery.mobile/images',
                src: '**.*',
                dest: '<%= dist_path %>/images'
            },
            'www-theme-images': {
                expand: true,
                cwd: 'src/css/jquery.mobile/flat-theme/',
                src: ['fonts/**/*.*', 'images/**/*.*'],
                dest: '<%= dist_path %>/'
            }
        },

        // HTML Procesor. Change the imports inside the html with the distribution paths and files
        processhtml: {
            www: {
                files: [{
                    expand: true,
                    cwd: 'src/',
                    src: '*.html',
                    dest: '<%= dist_path %>/'
                }]
            }
        },

        // Minify the HTML
        htmlmin: {
            www: {
                options: {
                    removeComments: true,
                    collapseWhitespace: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= dist_path %>',
                    src: '*.html',
                    dest: '<%= dist_path %>/'
                }]
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-http-server');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-csslint');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-processhtml');
    grunt.loadNpmTasks('grunt-contrib-htmlmin');

    /**
     * Default task when executing $grunt
     */
    grunt.registerTask('default', []);

    /**
     * Server Task. Starts an http server to test the application for the www task $grunt server
     */
    grunt.registerTask('server', [
        'http-server:src'
    ]);

    /**
     * Testing task $grunt www or $grunt watch
     */
    grunt.registerTask('test', [
        'jshint',
        'csslint'
    ]);

    /**
     * Distribution task $grunt dist
     */
    grunt.registerTask('dist', [
        'clean:www',
        'jshint',
        'csslint',
        'concat:js',
        'uglify',
        'concat:css',
        'cssmin',
        'copy:www-images',
        'copy:www-theme-images',
        'clean:www-js',
        'clean:www-css',
        'processhtml',
        'htmlmin',
        'http-server:www'
    ]);
}