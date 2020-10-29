android_lint.gradle_task = "lintProdDebug" # 実行するコマンドの設定、buildTypeやFlavorにあったコマンドを設定
android_lint.filtering = true # 変更されたファイルのみ指摘する．
android_lint.report_file = "app/build/reports/lint-results.xml" # コマンドを実行後に出力されるレポートファイルの場所を指定する
android_lint.lint(inline_mode: true) # PRコメントを行に直接書かれるようになる(Githubのみ)

findbugs.gradle_task = "spotBugs" #spotBugsを実行するためのコマンドを指定
findbugs.report_file = "app/build/reports/debug.xml" # コマンド実行した結果、出力されるxmlファイルの場所を指定
findbugs.report

github.dismiss_out_of_range_messages

checkstyle_format.base_path = Dir.pwd
Dir["*/app/build/reports/checkstyle.xml"].each do |file|
  checkstyle_format.report file
end
