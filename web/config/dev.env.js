'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  BASE_API: '"http://localhost:8080"'
  // BASE_API: '"http://172.20.48.250:8080"'
  // BASE_API: '"http://192.168.0.104:8080"'
})
