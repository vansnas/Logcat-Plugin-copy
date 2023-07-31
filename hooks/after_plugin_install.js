const fs = require('fs');
const path = require('path');

module.exports = function (context) {
  if (context.opts.platforms.includes('android')) {
    const platformRoot = path.join(context.opts.projectRoot, 'platforms', 'android');
    const buildGradlePath = path.join(platformRoot, 'app', 'build.gradle');

    if (fs.existsSync(buildGradlePath)) {
      const buildGradleContent = fs.readFileSync(buildGradlePath, 'utf8');

      if (!buildGradleContent.includes('implementation \'com.onesignal:OneSignal')) {
        const newBuildGradleContent = buildGradleContent.replace(
          'dependencies {',
          'dependencies {\n    implementation \'com.onesignal:OneSignal:4.4.0\''
        );

        fs.writeFileSync(buildGradlePath, newBuildGradleContent, 'utf8');
      }
    }
  }
};
