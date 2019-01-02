module.exports = {
    root: true,
    env: {
        browser: true
    },
    extends: [
        'plugin:vue/essential',
        'eslint:recommended',
        'standard' // https://github.com/standard/standard/blob/master/docs/RULES-en.md
    ],
    parserOptions: {
        parser: 'babel-eslint'
    },
    plugins: [
        'vue'
    ],
    rules: {
        'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off'
    }
}