var util = require('util'), webpack = require('webpack'), pkg = require('./package.json');
var WebpackDevServer = require('webpack-dev-server');

var host = pkg.config.devHost, port = pkg.config.devPort;

var configPath = process.argv[2] || './webpack.config';
var config = require(configPath);

var server = new WebpackDevServer(
    webpack(config),
    config.devServer
);

server.listen(port, host, function (err) {
    if (err) { console.log(err); }
    var url = util.format('http://%s:%d', host, port);
    console.log('Listening at %s', url);
})