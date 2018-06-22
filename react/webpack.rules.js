var path = require('path'), pkg = require('./package.json');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var DEBUG = process.env.NODE_ENV == 'development';

var cssLoader, sassLoader, sassParams = {
    'outputStyle': 'expanded',
    'includePaths': [path.resolve(__dirname, 'src/scss', path.resolve(__dirname, 'node_modules'))]
};

if (DEBUG) {
    cssLoader = ['style-loader', 'css-loader', 'postcss-loader'];
    sassLoader = ['style-loader', 'css-loader', 'postcss-loader', { loader: 'sass-loader', options: sassParams }];
} else {
    cssLoader = ExtractTextPlugin.extract({ fallback: 'style-loader', use: ['css-loader', 'postcss-loader'] });
    sassLoader = ExtractTextPlugin.extract({ fallback: 'style-loader', use: ['css-loader', 'postcss-loader', { loader: 'sass-loader', options: sassParams }] })
}

var rules = [{
    test: /\.jsx?$/,
    exclude: /node_modules/,
    use: [{ loader: 'babel-loader' }]
}, {
    test: /\.css$/,
    use: cssLoader
}, {
    test: /\.scss$/,
    use: sassLoader
}, {
    test: /\.jpe?g$|\.gif$|\.png$|\.ico|\.svg$|\.woff$|\.ttf$/,
    loader: [{ loader: 'file-loader', options: { 'name': '[path][name].[ext]' } }]
}, {
    test: /\.special\.json$/,
    type: 'javascript/auto',
    exclude: /node_modules/,
    use: 'special-loader'
}, {
    test: /\.html$/,
    loader: [{
        loader: 'file-loader',
        options: { 'name': '[path][name].[ext]' }
    }, {
        loader: 'template-html-loader',
        options: {
            'raw': true,
            'engine': 'lodash',
            'version': pkg.version,
            'title': pkg.name,
            'debug': DEBUG
        }
    }]
}];

module.exports = rules;