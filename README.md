Memory Game Master (Java Swing)
Java Swingで構築された、多機能な神経衰弱カードゲームです。
オブジェクト指向プログラミングの学習プロジェクトとして作成されました。

🚀 主な機能
GUIインターフェース: javax.swing を使用した直感的な操作画面。

3段階の難易度設定:

簡単 (60秒) / 普通 (30秒) / 難しい (15秒)

ハイスコア保存機能: 難易度ごとに最高残り秒数を記録し、highscores.properties ファイルに永続保存。

動的な演出:

制限時間カウントダウン（残り10秒で赤字警告）。

カード一致時のカラーフィードバック。

リトライ機能: ゲーム終了後に即座に再挑戦が可能。

サウンドフック: 効果音再生用のメソッドを実装済み。

🛠 実行環境 / 依存関係
Java JDK: 17以上推奨 (LTS)

ライブラリ: 標準ライブラリのみ（外部ライブラリ不要）

📂 フォルダ構成
Plaintext
.
├── src
│   └── MemoryGameMaster.java  # メインソースコード
├── res                        # リソースフォルダ
│   ├── flip.wav               # カードをめくる音
│   ├── match.wav              # 一致した時の音
│   ├── win.wav                # クリア音
│   └── tick.wav               # 秒読み音
├── highscores.properties      # スコア保存ファイル（自動生成）
└── README.md
💻 実行方法
リポジトリをクローンします。

Bash
git clone https://github.com/YourUsername/MemoryGameMaster.git
ソースコードをコンパイルします。

Bash
javac MemoryGameMaster.java
ゲームを実行します。

Bash
   java MemoryGameMaster
🎮 遊び方
アプリを起動すると難易度選択ダイアログが表示されます。

任意の難易度を選択してゲーム開始。

カード（ボタン）をクリックして、同じ数字のペアを探します。

制限時間内にすべてのペアを見つければクリア！

ハイスコアを更新すると、自動的にPCに記録が保存されます。

📝 今後のロードマップ (TODO)
[ ] カードを数字ではなく画像（ImageIcon）に変更。

[ ] BGMのON/OFF切り替え機能。

[ ] ネットワーク対戦モードの実装。
