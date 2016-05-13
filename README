This is your new Play application aaa
=====================================

This file will be packaged with your application, when using `play dist`.

## データベース設計

###Members
| column     | type        | other                    |
|:-----------|------------:|:------------------------:|
| id         | int         | primary key              |
| name       | String      |                          |
| password   | String      |                          |
| mail       | String      |                          |
| createTs   | date        |                          |
| updateTs   | date        |                          |
| versions   | int         |                          |

###Templates
| column     | type        | other                    |
|:-----------|------------:|:------------------------:|
| id         | int         | primary key              |
| memberId   | int         | foreign key(Members.id)  |
| name       | String      |                          |
| message    | String      |                          |
| html       | binary      |                          |
| flg        | int         |                          |
| createTs   | date        |                          |
| updateTs   | date        |                          |
| versions   | int         |                          |

###Color_Schemes
| column     | type        | other                    |
|:-----------|------------:|:------------------------:|
| id         | int         | primary key              |
| templateId | int         | foreign key(Templates.id)|
| className  | String      |                          |
| colorHex   | String      |                          |
| displayName| String      |                          |
| createTs   | date        |                          |
| updateTs   | date        |                          |
| versions   | int         |                          |


##タスク

###~5/22
| タスク                              | 名前         |
|:-----------------------------------|------------:|
| 仮のHTMLの完成                       | 海野         |

###~5/29
| タスク                              | 名前         |
|:-----------------------------------|------------:|
| リアルタイムでclassタグの色を変化させる  | 海野         |
| HTMLからclassタグのスクライビング      | 奥寺         |
| modelの作成                         | 奥寺         |
| ログイン機能の作成                    | 桃井         |
| 色の選択機能の実装                    | 桃井         |
