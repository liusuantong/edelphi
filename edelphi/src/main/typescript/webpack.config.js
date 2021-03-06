const path = require('path');

module.exports = {
  mode: "development",
  entry: './src/index.tsx',
  resolve: {
    alias: {
      '../../theme.config$': path.join(__dirname, 'semantic-theme/theme.config')
    },
    extensions: ['.ts', '.tsx', '.js', '.jsx']
  },
  output: {
    path: path.join(__dirname, "../webapp"),
    filename: '_scripts/dist/bundle.min.js'
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/, 
        loader: 'awesome-typescript-loader'
      },
      {
        test: /\.css$/,
        use: ['css-loader']
      },
      {
        test: /\.less$/,
        use: ['style-loader', 'css-loader', 'less-loader']
      },
      {
        test: /\.scss$/,
        use: [ "style-loader", "css-loader", "sass-loader"]
      },
      {
        test: /\.png$/,
        loader: "url-loader?mimetype=image/png" 
      },
      {
        test: /\.jpe?g$|\.gif$/,
        use: 'file-loader?name=[name].[ext]?[hash]'
      },
      {
        test: /\.woff(\?v=\d+\.\d+\.\d+)?$/,   
        loader: "url-loader?limit=10000&mimetype=application/font-woff&name=/_scripts/dist/[hash].[ext]"
      },
      {
        test: /\.woff2(\?v=\d+\.\d+\.\d+)?$/,  
        loader: "url-loader?limit=10000&mimetype=application/font-woff&name=/_scripts/dist/[hash].[ext]"
      },
      {
        test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,    
        loader: "url-loader?limit=10000&mimetype=application/octet-stream&name=/_scripts/dist/[name].[ext]"
      },
      {
        test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,    
        loader: "file-loader?name=/_scripts/dist/[name].[ext]"
      },
      {
        test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,    
        loader: "url-loader?limit=10000&mimetype=image/svg+xml&name=/_scripts/dist/[name].[ext]"
      }
    ]
  }
}