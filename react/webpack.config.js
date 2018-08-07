var path = require('path'), util = require('util'), pkg = require('./package.json');
var rules = require('./webpack.rules'), plugins = require('./webpack.plugins');
var DEBUG = process.env.NODE_ENV == 'development';

var entry = {
    'index': ['./index.jsx']
};

if (DEBUG) {
    entry.index.push(
        util.format(
            'webpack-dev-server/client?http://%s:%d',
            pkg.config.devHost,
            pkg.config.devPort
        )
    );
    entry.index.push('webpack/hot/dev-server');
}

var config = {
    mode: DEBUG ? 'development' : 'production',
    context: path.join(__dirname, 'src'),
    cache: DEBUG,
    target: 'web',
    devtool: DEBUG ? 'inline-source-map' : false,
    entry: entry,
    externals: {
        // 'antd': 'antd'
    },
    output: {
        path: path.resolve(pkg.config.buildDir),
        publicPath: '../',
        filename: path.join('js', util.format('[name].%s.js', pkg.version)),
        pathinfo: DEBUG
    },
    module: {
        rules: rules,
        // noParse: [
        //     path.join(__dirname, 'node_modules/antd')
        // ]
    },
    plugins: plugins,
    resolve: {
        // alias: {
        //     'antd': path.join(__dirname, 'node_modules/antd')
        // },
        extensions: ['.js', '.jsx', '.less', '.scss', '.css', '.json']
    },
    optimization: {
        splitChunks: {
            chunks: 'async',
            name: true
        }
    },
    devServer: {
        contentBase: path.resolve(pkg.config.buildDir),
        hot: DEBUG,
        noInfo: !DEBUG,
        inline: true,
        stats: { colors: true },
        proxy: { '/api/*': { target: pkg.config.devProxy, secure: false } }
    }
};

module.exports = config;