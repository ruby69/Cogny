const path = require('path');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const HTMLWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
  entry: {
    app: './src/app.js',
    signup: './src/signup.js',
    signupAuth: './src/signupAuth.js',
    // dashboard: './src/dashboard.js',
    // partner: './src/partner.js',
    // obd: './src/obd.js',
    // user: './src/user.js',
    // vehicle: './src/vehicle.js',
    // vmanage: './src/vmanage.js',
    // beagle: './src/beagle.js',
  },

  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'js/[name]-[hash].js'
  },

  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
      },
      {
        test: /\.css$/,
        use: ExtractTextPlugin.extract({
          fallback: "style-loader",
          use: "css-loader",
        })
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        options: {
          extractCSS: true
        },
      },
      {
        test: /\.(png|jpg|gif)$/,
        loader: 'file-loader',
        options: {
          outputPath: 'images',
          name: '[name].[ext]',
          publicPath: '/images/',
        },
      },
      {
        test: /\.(ttf|eot|woff|woff2|svg)$/,
        loader: 'file-loader',
        options: {
          outputPath: 'fonts',
          name: '[name].[ext]',
          publicPath: '/fonts/',
        },
      },
    ]
  },

  plugins: [
    new CleanWebpackPlugin([ 'dist' ]),
    new HTMLWebpackPlugin({
      filename: 'index.html',
      template: 'public/index.html',
      chunks: [ 'vendors', 'app' ],
    }),
    new HTMLWebpackPlugin({
      filename: 'signup/index.html',
      template: 'public/signup.html',
      chunks: [ 'vendors', 'signup' ],
    }),
    new HTMLWebpackPlugin({
      filename: 'signup/auth/index.html',
      template: 'public/signupAuth.html',
      chunks: [ 'vendors', 'signupAuth' ],
    }),    
    // new HTMLWebpackPlugin({
    //   filename: 'dashboard.html',
    //   template: 'public/dashboard.html',
    //   chunks: [ 'vendors', 'dashboard' ]
    // }),
    // new HTMLWebpackPlugin({
    //   filename: 'partner.html',
    //   template: 'public/partner.html',
    //   chunks: [ 'vendors', 'partner' ]
    // }),
    // new HTMLWebpackPlugin({
    //   filename: 'obd.html',
    //   template: 'public/obd.html',
    //   chunks: [ 'vendors', 'obd' ]
    // }),
    // new HTMLWebpackPlugin({
    //   filename: 'user.html',
    //   template: 'public/user.html',
    //   chunks: [ 'vendors', 'user' ]
    // }),
    // new HTMLWebpackPlugin({
    //   filename: 'vehicle.html',
    //   template: 'public/vehicle.html',
    //   chunks: [ 'vendors', 'vehicle' ]
    // }),
    // new HTMLWebpackPlugin({
    //   filename: 'vmanage.html',
    //   template: 'public/vmanage.html',
    //   chunks: [ 'vendors', 'vmanage' ]
    // }),
    // new HTMLWebpackPlugin({
    //   filename: 'beagle.html',
    //   template: 'public/beagle.html',
    //   chunks: [ 'vendors', 'beagle' ],
    // }),
    new ExtractTextPlugin('css/[name]-[hash].css'),
  ],

  optimization: {
    splitChunks: {
      cacheGroups: {
        commons: {
          test: /[\\/]node_modules[\\/]/,
          name: "vendors",
          chunks: "all"
        },
        // commons: {
        //   name: "vendors",
        //   chunks: "initial",
        //   minChunks: 2
        // },
      }
    }
  },

  devServer: {
    open: process.platform === 'darwin',
    host: '0.0.0.0',
    port: 80,
    https: false,
    hotOnly: false,
    disableHostCheck: true,
    historyApiFallback: true,

    before: app => {
      // app is an express instance
    },

    watchOptions: {
      aggregateTimeout: 300,
      poll: 5000
    },

    proxy: {
      "/api/*": {
        target: "http://10.0.75.1:8080",
        changeOrigin: true,
        xfwd : true
      }
    }
  }
};
