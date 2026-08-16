<template>
  <div class="ai-chat">
    <div class="chat-container">
      <div class="chat-header">
        <h2>🎵 AI音乐助手</h2>
        <p>我可以帮您查找和推荐音乐哦~</p>
      </div>

      <div class="chat-messages" ref="chatMessages">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
          <div class="message-content">
            <div class="message-text">{{ msg.content }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <textarea v-model="inputMessage" placeholder="输入您的问题..." @keyup.enter="sendMessage"></textarea>
        <button @click="sendMessage" :disabled="!inputMessage.trim()">发送</button>
      </div>
    </div>
  </div>
</template>

<script>
import { HttpManager } from '../api/index'
import { mapGetters } from 'vuex'

export default {
  name: 'ai-chat',
  data () {
    return {
      inputMessage: '',
      messages: [
        {
          role: 'ai',
          content: '您好！我是您的AI音乐助手，我可以帮您查找和推荐音乐。请问您想听什么类型的音乐？'
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['userId'])
  },
  watch: {
    messages: {
      handler () {
        this.$nextTick(() => {
          const chatMessages = this.$refs.chatMessages
          if (chatMessages) {
            chatMessages.scrollTop = chatMessages.scrollHeight
          }
        })
      },
      deep: true
    }
  },
  methods: {
    async sendMessage () {
      if (!this.inputMessage.trim()) return
      if (!this.userId) {
        this.$notify('请先登录', 'warning')
        return
      }

      const userMessage = this.inputMessage.trim()
      this.messages.push({
        role: 'user',
        content: userMessage
      })
      this.inputMessage = ''

      this.messages.push({
        role: 'ai-loading',
        content: '正在思考...'
      })

      try {
        const res = await HttpManager.sendChatMessage(this.userId, userMessage)
        this.messages.pop()
        
        if (res && res.aiMessage) {
          this.messages.push({
            role: 'ai',
            content: res.aiMessage
          })
        } else {
          this.messages.push({
            role: 'ai',
            content: '抱歉，我暂时无法回复您的问题。'
          })
        }
      } catch (err) {
        this.messages.pop()
        console.log(err)
        this.$notify('发送失败，请稍后重试', 'error')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.ai-chat {
  position: fixed;
  top: 120px;
  left: 0;
  right: 0;
  bottom: 120px;
  z-index: 1000;
  background: #f5f5f5;

  .chat-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .chat-header {
      padding: 20px;
      background: #e0e0e0;
      color: #333;

      h2 {
        margin: 0 0 10px 0;
        font-size: 24px;
      }

      p {
        margin: 0;
        opacity: 0.9;
      }
    }

    .chat-messages {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      background: #fafafa;

      .message {
        margin-bottom: 15px;
        display: flex;

        &.user {
          justify-content: flex-end;

          .message-content {
            background: #e0e0e0;
            color: #333;
            padding: 10px 15px;
            border-radius: 15px 15px 0 15px;
            max-width: 70%;
          }
        }

        &.ai {
          justify-content: flex-start;

          .message-content {
            background: white;
            color: #333;
            padding: 10px 15px;
            border-radius: 15px 15px 15px 0;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
            max-width: 70%;

            .message-text {
              white-space: pre-wrap;
            }
          }
        }

        &.ai-loading {
          justify-content: flex-start;

          .message-content {
            color: #999;
            padding: 10px 15px;
          }
        }
      }
    }

    .chat-input {
      padding: 20px;
      border-top: 1px solid #eee;
      display: flex;
      gap: 10px;

      textarea {
        flex: 1;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 5px;
        resize: none;
        height: 60px;
        font-family: inherit;

        &:focus {
          outline: none;
          border-color: #bdbdbd;
        }
      }

      button {
        padding: 10px 20px;
        background: #e0e0e0;
        color: #333;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 14px;

        &:hover {
          background: #d0d0d0;
        }

        &:disabled {
          background: #ccc;
          cursor: not-allowed;
        }
      }
    }
  }
}
</style>
