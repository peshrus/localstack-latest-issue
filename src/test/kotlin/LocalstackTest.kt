import io.kotest.core.spec.style.DescribeSpec
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class LocalstackTest : DescribeSpec({
    describe("Localstack") {
        it("starts S3 & SQS with version 1.4") {
            startAndStopLocalstack("1.4", "/docker-entrypoint-initaws.d/init.sh")
        }

        // Works locally on macOS
        it("starts S3 & SQS with the latest version") {
            startAndStopLocalstack("latest", "/etc/localstack/init/ready.d/init.sh")
        }
    }
})

private fun startAndStopLocalstack(version: String, containerPath: String) {
    LocalStackContainer(DockerImageName.parse("localstack/localstack:$version")).apply {
        waitingFor(Wait.forLogMessage(".*set \\+x.*", 1))
        withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.SQS)
        withFileSystemBind(
            LocalstackTest::class.java.getResource("init-localstack.sh")!!.path,
            containerPath,
            BindMode.READ_ONLY
        )
        start()
        stop()
    }
}
