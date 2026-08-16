<template>
  <div class="recommend">
    <div class="recommend-header">
      <h2>🎵 歌曲推荐</h2>
      <div class="recommend-tabs">
        <button :class="{ active: tabIndex === 0 }" @click="switchTab(0)">👤 协同过滤</button>
        <button :class="{ active: tabIndex === 1 }" @click="switchTab(1)">📚 基于内容</button>
        <button :class="{ active: tabIndex === 2 }" @click="switchTab(2)">🔥 热门歌曲</button>
      </div>
    </div>

    <div class="recommend-content">
      <div v-if="loading" class="loading">
        <div class="spinner">
          <div class="rect1"></div>
          <div class="rect2"></div>
          <div class="rect3"></div>
          <div class="rect4"></div>
          <div class="rect5"></div>
        </div>
        <p>正在加载推荐...</p>
      </div>

      <div v-else-if="recommendations.length === 0" class="empty">
        <p>暂无推荐歌曲</p>
      </div>

      <div v-else>
        <content-list :contentList="recommendations" path="lyric"></content-list>
      </div>
    </div>

    <div class="recommend-footer">
      <div class="stats">
        <span>共推荐 {{ total }} 首歌曲</span>
      </div>
    </div>
  </div>
</template>

<script>
import { HttpManager } from '../api/index'
import { mapGetters } from 'vuex'
import mixin from '../mixins'
import ContentList from '../components/ContentList'

export default {
  name: 'recommend',
  mixins: [mixin],
  components: {
    ContentList
  },
  data () {
    return {
      tabIndex: 0,
      recommendations: [],
      loading: false,
      total: 0,
      limit: 20
    }
  },
  computed: {
    ...mapGetters(['userId'])
  },
  created () {
    this.loadRecommendations()
  },
  methods: {
    switchTab (index) {
      this.tabIndex = index
      this.loadRecommendations()
    },
    loadRecommendations () {
      if (!this.userId) {
        this.$notify('请先登录', 'warning')
        return
      }

      this.loading = true
      let api

      switch (this.tabIndex) {
        case 0:
          api = HttpManager.getCollaborativeRecommend
          break
        case 1:
          api = HttpManager.getContentRecommend
          break
        case 2:
          api = HttpManager.getPopularSongs
          break
      }

      api(this.userId, this.limit)
        .then(res => {
          this.loading = false
          if (res.code === 1) {
            this.recommendations = res.data
            this.total = res.total
          } else {
            this.$notify(res.msg, 'warning')
          }
        })
        .catch(err => {
          this.loading = false
          console.log(err)
          this.$notify('加载失败，请稍后重试', 'error')
        })
    },
    getSingerName (singerId) {
      const singers = this.$store.state.singerList || []
      const singer = singers.find(s => s.id === singerId)
      return singer ? singer.name : '未知歌手'
    }
  }
}
</script>

<style lang="scss" scoped>
.recommend {
  padding: 20px;
  min-height: calc(100vh - 200px);

  .recommend-header {
    margin-bottom: 30px;

    h2 {
      font-size: 28px;
      color: #333;
      margin-bottom: 20px;
    }

    .recommend-tabs {
      display: flex;
      gap: 10px;

      button {
        padding: 10px 20px;
        border: none;
        background: #f5f5f5;
        border-radius: 20px;
        cursor: pointer;
        font-size: 14px;
        transition: all 0.3s;

        &:hover {
          background: #e0e0e0;
        }

        &.active {
          background: #409eff;
          color: white;
        }
      }
    }
  }

  .recommend-content {
    .loading {
      text-align: center;
      padding: 60px 0;
      color: #999;

      .spinner {
        display: inline-block;
        width: 50px;
        height: 50px;
        margin-bottom: 20px;

        div {
          width: 10px;
          height: 10px;
          background-color: #409eff;
          border-radius: 50%;
          display: inline-block;
          animation: sk-bouncedelay 1.4s infinite ease-in-out both;
          margin: 0 5px;
        }

        .rect1 { animation-delay: -0.32s; }
        .rect2 { animation-delay: -0.16s; }

        @keyframes sk-bouncedelay {
          0%, 80%, 100% { transform: scale(0); }
          40% { transform: scale(1); }
        }
      }
    }

    .empty {
      text-align: center;
      padding: 60px 0;
      color: #999;
    }
  }

  .recommend-footer {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #eee;

    .stats {
      text-align: center;
      color: #999;
    }
  }
}
</style>
