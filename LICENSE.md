Last login: Tue Nov  4 12:13:31 on console
Ayoub@Ayoubs-MacBook-Pro ~ % ls
Applications	Documents	Library		Music		Pictures
Desktop		Downloads	Movies		Parallels	Public
Ayoub@Ayoubs-MacBook-Pro ~ % ls -la
total 40
drwxr-x---+ 18 Ayoub  staff    576  5 nov. 08:03 .
drwxr-xr-x   6 root   admin    192  4 nov. 12:12 ..
drwx------@ 10 Ayoub  staff    320  4 nov. 10:10 .BurpSuite
-r--------   1 Ayoub  staff      7  3 nov. 19:58 .CFUserTextEncoding
-rw-r--r--@  1 Ayoub  staff  10244  3 nov. 15:21 .DS_Store
drwx------+  7 Ayoub  staff    224  4 nov. 12:14 .Trash
-rw-------   1 Ayoub  staff     94  4 nov. 10:04 .zsh_history
drwx------  14 Ayoub  staff    448  5 nov. 08:03 .zsh_sessions
drwx------@  4 Ayoub  staff    128  4 nov. 11:08 Applications
drwx------+  5 Ayoub  staff    160  4 nov. 12:14 Desktop
drwx------+  3 Ayoub  staff     96 30 okt. 16:55 Documents
drwx------+ 14 Ayoub  staff    448  5 nov. 07:16 Downloads
drwx------@ 79 Ayoub  staff   2528  3 nov. 20:01 Library
drwx------   3 Ayoub  staff     96 30 okt. 16:55 Movies
drwx------+  4 Ayoub  staff    128 30 okt. 16:58 Music
drwx------@  3 Ayoub  staff     96  4 nov. 10:23 Parallels
drwx------+  4 Ayoub  staff    128 30 okt. 16:56 Pictures
drwxr-xr-x+  4 Ayoub  staff    128 30 okt. 16:55 Public
Ayoub@Ayoubs-MacBook-Pro ~ % ssh-keygen -t ed25519 -C "ayoub.benlamchich@intigriti.com"
Generating public/private ed25519 key pair.
Enter file in which to save the key (/Users/Ayoub/.ssh/id_ed25519): 
Created directory '/Users/Ayoub/.ssh'.
Enter passphrase for "/Users/Ayoub/.ssh/id_ed25519" (empty for no passphrase): 
Enter same passphrase again: 
Your identification has been saved in /Users/Ayoub/.ssh/id_ed25519
Your public key has been saved in /Users/Ayoub/.ssh/id_ed25519.pub
The key fingerprint is:
SHA256:vVq59zI2xtQBfmeiqzaIItnVpFgTqUmpX/ZGO876OoY ayoub.benlamchich@intigriti.com
The key's randomart image is:
+--[ED25519 256]--+
|     . .         |
|    o o     .    |
|   o o .   . .   |
|  . o = o.  . + o|
|   . = BS..  + = |
|    o o *  oo .  |
|   o o = o+o .   |
|  o E + +oo.X    |
|   . oo=o.o* =.  |
+----[SHA256]-----+
Ayoub@Ayoubs-MacBook-Pro ~ % eval "$(ssh-agent -s)"
Agent pid 6132
Ayoub@Ayoubs-MacBook-Pro ~ % open ~/.ssh/config
The file /Users/Ayoub/.ssh/config does not exist.
Ayoub@Ayoubs-MacBook-Pro ~ % touch ~/.ssh/config
Ayoub@Ayoubs-MacBook-Pro ~ % open ~/.ssh/config 
Ayoub@Ayoubs-MacBook-Pro ~ % open ~/.ssh/config
Ayoub@Ayoubs-MacBook-Pro ~ % ssh-add --apple-use-keychain ~/.ssh/id_ed25519
Identity added: /Users/Ayoub/.ssh/id_ed25519 (ayoub.benlamchich@intigriti.com)
Ayoub@Ayoubs-MacBook-Pro ~ % pbcopy < ~/.ssh/id_ed25519.pub
Ayoub@Ayoubs-MacBook-Pro ~ % ls
Applications	Documents	Library		Music		Pictures
Desktop		Downloads	Movies		Parallels	Public
Ayoub@Ayoubs-MacBook-Pro ~ % mkdir Intigriti
Ayoub@Ayoubs-MacBook-Pro ~ % cd Intigriti
Ayoub@Ayoubs-MacBook-Pro Intigriti % git clone git@github.com:intigriti/quick-scope.git
Cloning into 'quick-scope'...
The authenticity of host 'github.com (140.82.121.4)' can't be established.
ED25519 key fingerprint is SHA256:+DiY3wvvV6TuJJhbpZisF/zLDA0zPMSvHdkr4UvCOqU.
This key is not known by any other names.
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
Warning: Permanently added 'github.com' (ED25519) to the list of known hosts.
warning: You appear to have cloned an empty repository.
Ayoub@Ayoubs-MacBook-Pro Intigriti % ls
quick-scope
Ayoub@Ayoubs-MacBook-Pro Intigriti % cd quick-scope 
Ayoub@Ayoubs-MacBook-Pro quick-scope % ls
Ayoub@Ayoubs-MacBook-Pro quick-scope % ls
quickscope-main.zip
Ayoub@Ayoubs-MacBook-Pro quick-scope % unzip quickscope-main.zip 
Archive:  quickscope-main.zip
ea239f96de03322ed92387d1a658519c45a9b165
   creating: quickscope-main
   creating: quickscope-main/.github
  inflating: quickscope-main/.github/dependabot.yml  
   creating: quickscope-main/.github/workflows
  inflating: quickscope-main/.github/workflows/.security.yml  
  inflating: quickscope-main/.github/workflows/lint.yml  
  inflating: quickscope-main/.github/workflows/release.yml  
  inflating: quickscope-main/.gitignore  
  inflating: quickscope-main/README.md  
  inflating: quickscope-main/build.gradle  
 extracting: quickscope-main/settings.gradle  
   creating: quickscope-main/src
   creating: quickscope-main/src/main
   creating: quickscope-main/src/main/java
   creating: quickscope-main/src/main/java/com
   creating: quickscope-main/src/main/java/com/quickscope
  inflating: quickscope-main/src/main/java/com/quickscope/QuickScopeExtension.java  
   creating: quickscope-main/src/main/java/com/quickscope/api
  inflating: quickscope-main/src/main/java/com/quickscope/api/IntigritiApiClient.java  
   creating: quickscope-main/src/main/java/com/quickscope/api/models
  inflating: quickscope-main/src/main/java/com/quickscope/api/models/Domain.java  
  inflating: quickscope-main/src/main/java/com/quickscope/api/models/Program.java  
  inflating: quickscope-main/src/main/java/com/quickscope/api/models/ProgramDetails.java  
   creating: quickscope-main/src/main/java/com/quickscope/config
  inflating: quickscope-main/src/main/java/com/quickscope/config/ExtensionConfig.java  
  inflating: quickscope-main/src/main/java/com/quickscope/config/QuickScopeConfig.java  
   creating: quickscope-main/src/main/java/com/quickscope/ui
  inflating: quickscope-main/src/main/java/com/quickscope/ui/ConfigPanel.java  
  inflating: quickscope-main/src/main/java/com/quickscope/ui/DomainTable.java  
  inflating: quickscope-main/src/main/java/com/quickscope/ui/DomainTableModel.java  
  inflating: quickscope-main/src/main/java/com/quickscope/ui/DomainsPanel.java  
  inflating: quickscope-main/src/main/java/com/quickscope/ui/ProgramTable.java  
   creating: quickscope-main/src/main/java/com/quickscope/util
  inflating: quickscope-main/src/main/java/com/quickscope/util/AdvancedScopeUtil.java  
  inflating: quickscope-main/src/main/java/com/quickscope/util/EndpointClassifier.java  
  inflating: quickscope-main/src/main/java/com/quickscope/util/RequirementsManager.java  
  inflating: quickscope-main/src/main/java/com/quickscope/util/ScopeConverter.java  
   creating: quickscope-main/src/resources
   creating: quickscope-main/src/resources/burp
  inflating: quickscope-main/src/resources/burp/manifest.json  
   creating: quickscope-main/test
   creating: quickscope-main/test/java
   creating: quickscope-main/test/java/com
   creating: quickscope-main/test/java/com/inscope
   creating: quickscope-main/test/java/com/inscope/api
 extracting: quickscope-main/test/java/com/inscope/api/IntigritiApiClientTest.java  
Ayoub@Ayoubs-MacBook-Pro quick-scope % ls
quickscope-main		quickscope-main.zip
Ayoub@Ayoubs-MacBook-Pro quick-scope % mv quickscope-main .
mv: quickscope-main and ./quickscope-main are identical
Ayoub@Ayoubs-MacBook-Pro quick-scope % mv quickscope-main/* .
Ayoub@Ayoubs-MacBook-Pro quick-scope % ls
build.gradle		quickscope-main.zip	settings.gradle		test
quickscope-main		README.md		src
Ayoub@Ayoubs-MacBook-Pro quick-scope % rmdir quickscope-main
rmdir: quickscope-main: Directory not empty
Ayoub@Ayoubs-MacBook-Pro quick-scope % ls -la quickscope-main
total 8
drwxr-xr-x@  4 Ayoub  staff  128  5 nov. 08:07 .
drwxr-xr-x  10 Ayoub  staff  320  5 nov. 08:07 ..
drwxr-xr-x@  4 Ayoub  staff  128  9 jun. 10:39 .github
-rw-r--r--@  1 Ayoub  staff  144  9 jun. 10:39 .gitignore
Ayoub@Ayoubs-MacBook-Pro quick-scope % mv quickscope-main/.git* . 
Ayoub@Ayoubs-MacBook-Pro quick-scope % ls
build.gradle		quickscope-main.zip	settings.gradle		test
quickscope-main		README.md		src
Ayoub@Ayoubs-MacBook-Pro quick-scope % rmdir quickscope-main
Ayoub@Ayoubs-MacBook-Pro quick-scope % ls
build.gradle		README.md		src
quickscope-main.zip	settings.gradle		test
Ayoub@Ayoubs-MacBook-Pro quick-scope % git add .
Ayoub@Ayoubs-MacBook-Pro quick-scope % git commit -m "feat: Repo transfer"
[main (root-commit) 07ac4fb] feat: Repo transfer
 Committer: Ayoub Benlamchich <Ayoub@Ayoubs-MacBook-Pro.local>
Your name and email address were configured automatically based
on your username and hostname. Please check that they are accurate.
You can suppress this message by setting them explicitly. Run the
following command and follow the instructions in your editor to edit
your configuration file:

    git config --global --edit

After doing this, you may fix the identity used for this commit with:

    git commit --amend --reset-author

 27 files changed, 4690 insertions(+)
 create mode 100644 .github/dependabot.yml
 create mode 100644 .github/workflows/.security.yml
 create mode 100644 .github/workflows/lint.yml
 create mode 100644 .github/workflows/release.yml
 create mode 100644 .gitignore
 create mode 100644 README.md
 create mode 100644 build.gradle
 create mode 100644 quickscope-main.zip
 create mode 100644 settings.gradle
 create mode 100644 src/main/java/com/quickscope/QuickScopeExtension.java
 create mode 100644 src/main/java/com/quickscope/api/IntigritiApiClient.java
 create mode 100644 src/main/java/com/quickscope/api/models/Domain.java
 create mode 100644 src/main/java/com/quickscope/api/models/Program.java
 create mode 100644 src/main/java/com/quickscope/api/models/ProgramDetails.java
 create mode 100644 src/main/java/com/quickscope/config/ExtensionConfig.java
 create mode 100755 src/main/java/com/quickscope/config/QuickScopeConfig.java
 create mode 100644 src/main/java/com/quickscope/ui/ConfigPanel.java
 create mode 100644 src/main/java/com/quickscope/ui/DomainTable.java
 create mode 100644 src/main/java/com/quickscope/ui/DomainTableModel.java
 create mode 100644 src/main/java/com/quickscope/ui/DomainsPanel.java
 create mode 100644 src/main/java/com/quickscope/ui/ProgramTable.java
 create mode 100644 src/main/java/com/quickscope/util/AdvancedScopeUtil.java
 create mode 100644 src/main/java/com/quickscope/util/EndpointClassifier.java
 create mode 100644 src/main/java/com/quickscope/util/RequirementsManager.java
 create mode 100644 src/main/java/com/quickscope/util/ScopeConverter.java
 create mode 100644 src/resources/burp/manifest.json
 create mode 100644 test/java/com/inscope/api/IntigritiApiClientTest.java
Ayoub@Ayoubs-MacBook-Pro quick-scope % git branch -M main  
Ayoub@Ayoubs-MacBook-Pro quick-scope % git push origin main
Enumerating objects: 48, done.
Counting objects: 100% (48/48), done.
Delta compression using up to 8 threads
Compressing objects: 100% (37/37), done.
Writing objects: 100% (48/48), 72.36 KiB | 10.34 MiB/s, done.
Total 48 (delta 1), reused 0 (delta 0), pack-reused 0 (from 0)
remote: Resolving deltas: 100% (1/1), done.
To github.com:intigriti/quick-scope.git
 * [new branch]      main -> main
Ayoub@Ayoubs-MacBook-Pro quick-scope % nano LICENSE.md

  UW PICO 5.09                                        File: LICENSE.md                                        Modified  

MIT License

Copyright (c) 2025 Intigriti

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.





^G Get Help         ^O WriteOut         ^R Read File        ^Y Prev Pg          ^K Cut Text         ^C Cur Pos          
^X Exit             ^J Justify          ^W Where is         ^V Next Pg          ^U UnCut Text       ^T To Spell        
