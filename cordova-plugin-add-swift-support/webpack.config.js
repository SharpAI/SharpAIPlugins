module.exports = {
  entry: './src/main.js',
  target: 'node',
  output: {
    libraryTarget: 'umd',
    filename: './add-swift-support.js'
  },
  module: {
    rules: [
      {
        test: /\.js?$/,
        exclude: /(node_modules)/,
        loader: 'babel-loader',
        options: {
          presets: ['es2015'],
          plugins: [
            'add-module-exports'
          ]
        }
      }
    ]
  }
};
