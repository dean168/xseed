import axios from 'axios'
import { Loading, Message } from 'element-ui'
import router from './router'

export const exchange = (options) => {
  return new Promise((resolve, reject) => {
    fetch(options).then(status => {
      if (status.errcode === 200) {
        resolve(status.data)
      } else if (status.errcode === 401) { // 未授权、未登录
        router.push('/login')
      } else if (status.errcode === 403) { // 拒绝访问、无权限查看
        Message({ type: 'error', message: status.message })
      } else {
        console.error(options.method + ' ' + options.url, status)
        Message({ type: 'error', message: status.message })
      }
    }).catch(error => {
      Message({ type: 'error', message: '服务器异常，请联系技术人员处理' })
      reject(error)
    })
  })
}

export const fetch = (options) => {
  return new Promise((resolve, reject) => {
    let animate = options.animate === undefined || options.animate // 默认有蒙层
    let loading = animate ? Loading.service({ lock: true, text: 'Loading', spinner: 'el-icon-loading' }) : undefined
    axios[options.method](options.url, options.data).then(res => {
      loading && loading.close()
      resolve(res.data)
    }).catch(error => {
      loading && loading.close()
      reject(error)
    })
  })
}
