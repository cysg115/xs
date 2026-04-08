# 🔧 GitHub Actions 故障排查指南

如果你在GitHub Actions页面左侧看不到"Build APK"工作流，请按以下步骤排查：

## 🔍 **问题：看不到"Build APK"工作流**

### **可能原因**
1. 工作流文件未正确上传
2. 文件路径或命名错误
3. GitHub需要时间处理
4. 工作流文件语法错误

---

## 🚀 **解决方案**

### **第一步：检查文件结构**

登录GitHub，检查仓库文件结构，确保有以下文件：

```
你的仓库名/
├── .github/
│   └── workflows/
│       ├── build-apk.yml          # 主工作流
│       └── android-build.yml      # 备用工作流（新增）
├── app/
├── gradle/
└── gradlew
```

**检查方法：**
1. 进入GitHub仓库页面
2. 查看是否有 `.github/workflows/` 文件夹
3. 点击进入该文件夹，查看是否有YAML文件

---

### **第二步：手动触发工作流**

如果看不到工作流，但文件确实存在：

1. **进入Actions页面**：`https://github.com/你的用户名/仓库名/actions`
2. **点击"New workflow"**（绿色按钮）
3. **搜索工作流**：
   - 在搜索框输入 `Android`
   - 或滚动查找
4. **如果看到"Android Build"**，点击"Configure"

---

### **第三步：重新上传工作流文件**

如果文件确实缺失：

#### **方法A：网页上传**
1. 在仓库点击 "Add file" → "Upload files"
2. 上传以下文件：
   - `.github/workflows/build-apk.yml`
   - `.github/workflows/android-build.yml`
3. 点击 "Commit changes"

#### **方法B：Git命令**
```bash
# 确保在项目目录
cd LocalMP3Player

# 重新添加工作流文件
git add .github/workflows/
git commit -m "添加GitHub Actions工作流"
git push origin main
```

---

### **第四步：等待GitHub处理**

GitHub有时需要几分钟识别新工作流：

1. 上传文件后等待 **2-3分钟**
2. 刷新Actions页面
3. 检查左侧工作流列表

---

### **第五步：检查工作流状态**

在仓库页面：
1. 点击顶部 **"Actions"** 标签
2. 查看是否有任何工作流运行记录
3. 如果有运行记录但失败，点击查看错误信息

---

## ⚡ **快速修复方案**

### **方案A：使用备用工作流**
如果 `build-apk.yml` 不显示，可以：
1. 删除 `.github/workflows/build-apk.yml`
2. 只保留 `android-build.yml`
3. 重新提交并推送

### **方案B：创建简单测试工作流**
1. 在GitHub仓库页面点击 "Add file" → "Create new file"
2. 文件路径：`.github/workflows/test.yml`
3. 内容：
```yaml
name: Test Workflow
on: [push, workflow_dispatch]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - run: echo "Workflow is working!"
```
4. 点击 "Commit new file"
5. 等待1分钟，检查是否显示

---

## 🐛 **常见错误及解决**

### **错误1：找不到工作流文件**
```
Error: Unable to find workflow file
```
**解决**：确保文件路径为 `.github/workflows/`（注意前面的点）

### **错误2：YAML语法错误**
```
Invalid YAML
```
**解决**：使用YAML验证工具检查语法

### **错误3：权限问题**
```
Permission denied
```
**解决**：确保文件在正确的分支（main/master）

### **错误4：未触发工作流**
```
No workflow runs
```
**解决**：
1. 确保文件在 `.github/workflows/` 目录
2. 尝试推送新更改到仓库
3. 手动触发：Actions → 选择工作流 → Run workflow

---

## 📱 **手动触发构建（无工作流显示）**

如果还是看不到工作流，可以直接通过URL触发：

### **方法A：使用API触发**
1. 访问：`https://github.com/你的用户名/仓库名/actions/workflows/android-build.yml`
2. 点击 **"Run workflow"**
3. 选择分支：`main`
4. 点击绿色按钮

### **方法B：创建简单工作流**
1. 在GitHub网页编辑器创建新文件：
   - 路径：`.github/workflows/simple-build.yml`
   - 内容使用上面的 `android-build.yml`
2. 提交后立即生效

---

## 🔄 **工作流文件对比**

| 文件 | 功能 | 状态 |
|------|------|------|
| `build-apk.yml` | 完整构建，含中文注释 | **主推荐** |
| `android-build.yml` | 简化版，英文 | **备用** |

**建议**：两个文件都上传，至少一个会被识别。

---

## 🎯 **验证步骤**

请按顺序检查：

1. [ ] **仓库是否有 `.github/workflows/` 文件夹？**
   - 有 → 继续第2步
   - 没有 → 创建文件夹并上传文件

2. [ ] **文件夹内是否有YAML文件？**
   - 有 → 继续第3步
   - 没有 → 上传 `build-apk.yml` 和 `android-build.yml`

3. [ ] **GitHub Actions页面是否显示工作流？**
   - 显示 → 成功！点击运行
   - 不显示 → 等待2分钟刷新

4. [ ] **能否手动触发？**
   - 能 → 开始构建
   - 不能 → 尝试"快速修复方案"

---

## 📞 **如果所有方法都失败**

### **最后的手段：从头开始**

1. **创建新仓库**：
   ```bash
   # 本地操作
   rm -rf .github
   git add .
   git commit -m "移除旧工作流"
   git push origin main
   
   # 等待1分钟
   
   # 重新创建工作流
   mkdir -p .github/workflows
   cp android-build.yml .github/workflows/
   git add .github/
   git commit -m "添加简单工作流"
   git push origin main
   ```

2. **使用纯文本工作流**：
   - 避免中文字符
   - 使用最基本语法
   - 先确保能运行，再添加功能

---

## ⏱️ **时间线参考**

| 操作 | 预计时间 |
|------|----------|
| 上传工作流文件 | 1分钟 |
| GitHub识别 | 1-3分钟 |
| 手动触发构建 | 1分钟 |
| 构建过程 | 3-5分钟 |
| 下载APK | 1分钟 |

**总耗时**：7-10分钟

---

## ✅ **成功标志**

当你看到以下界面时，表示成功：

1. **Actions页面**左侧显示工作流列表
2. **工作流运行记录**显示"正在运行"
3. **绿色对勾**表示构建成功
4. **Artifacts区域**出现可下载的APK

---

## 🆘 **紧急求助**

如果仍然无法解决，请提供：

1. **GitHub仓库URL**（我可以查看结构）
2. **错误截图**（Actions页面）
3. **上传的文件列表截图**

我将为你提供针对性解决方案！

---

**记住**：GitHub Actions有时会有延迟，耐心等待2-3分钟再刷新页面。大部分情况下，只要文件在正确位置，最终都会被识别。✨