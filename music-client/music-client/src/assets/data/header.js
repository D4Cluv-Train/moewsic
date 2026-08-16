// 左侧导航栏
const navMsg = [{
  name: '首页',
  path: '/'
}, {
  name: '歌曲推荐',
  path: '/recommend'
}, {
  name: 'AI对话',
  path: '/ai-chat'
}, {
  name: '歌单列表',
  path: '/song-list'
}, {
  name: '歌手列表',
  path: '/singer'
}, {
  name: '我的收藏',
  path: '/my-music'
}]

// 右侧导航栏
const loginMsg = [{
  name: '登录',
  path: '/login-in'
}, {
  name: '注册',
  path: '/sign-up'
}]

// 用户下拉菜单项
const menuList = [{
  name: '设置',
  path: '/setting'
}, {
  name: '退出',
  path: 0
}]

export {
  navMsg,
  loginMsg,
  menuList
}
