var path = require('path'), util = require('util'), webpack = require('webpack'), autoprefixer = require('autoprefixer'), pkg = require('./package.json');
var ExtractTextPlugin = require('extract-text-webpack-plugin'), CopyWebpackPlugin = require('copy-webpack-plugin');
var DEBUG = process.env.NODE_ENV == 'development';
var cssBundle = path.join('css', util.format('[name].%s.css', pkg.version));

var plugins = [
    new CopyWebpackPlugin([{
        from: path.join(__dirname, 'node_modules/bootstrap/dist/css/bootstrap.min.css'),
        to: 'css/bootstrap.min.css'
    }, {
        from: path.join(__dirname, 'node_modules/bootstrap/dist/css/bootstrap.min.css.map'),
        to: 'css/bootstrap.min.css.map'
    }, {
    //     from: path.join(__dirname, 'node_modules/antd/dist/antd.min.js'),
    //     to: 'js/antd.min.js'
    // }, {
    //     from: path.join(__dirname, 'node_modules/antd/dist/antd.min.js.map'),
    //     to: 'js/antd.min.js.map'
    // }, {
    //     from: path.join(__dirname, 'node_modules/antd/dist/antd.min.css'),
    //     to: 'css/antd.min.css'
    // }, {
    //     from: path.join(__dirname, 'node_modules/antd/dist/antd.min.css.map'),
    //     to: 'css/antd.min.css.map'
    // }, {
    //     from: path.join(__dirname, 'node_modules/bootstrap/dist/fonts'),
    //     to: 'fonts'
    // }, {
        from: 'assets/i18n',
        to: 'i18n'
    }, {
        from: 'assets/images',
        to: 'images'
    }]),
    // new webpack.ProvidePlugin({
    //     i18n: 'i18n'
    // }),
    new webpack.optimize.OccurrenceOrderPlugin()
];

if (DEBUG) {
    plugins.push(
        new webpack.HotModuleReplacementPlugin(),
        new webpack.LoaderOptionsPlugin({
            debug: DEBUG,
            options: {
                context: path.join(__dirname, 'src'),
                postcss: [autoprefixer]
            }
        })
    )
} else {
    plugins.push(new ExtractTextPlugin(cssBundle, { allChunks: true }));
}

module.exports = plugins;